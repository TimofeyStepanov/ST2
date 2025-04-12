package ru.stepanoff.helper.impl;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import ru.stepanoff.constants.TriggerConditionOperator;
import ru.stepanoff.constants.TriggerConditionSign;
import ru.stepanoff.dto.ConfigurationDTO;
import ru.stepanoff.dto.InputTopicDTO;
import ru.stepanoff.dto.TopicFieldDTO;
import ru.stepanoff.helper.SqlRequestCreatorHelper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.stepanoff.constants.TriggerConditionDelimiter.DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD;


public abstract class AbstractSqlRequestCreatorHelper implements SqlRequestCreatorHelper {
    protected AbstractSqlRequestCreatorHelper(String dbName) {
        this.dbName = dbName;
    }

    /**
     * Граф. Используется для построения select запроса
     */
    private static class Graph<V> implements Iterable<Graph.Node<V>> {
        /**
         * Ребро графа
         */
        @EqualsAndHashCode
        @AllArgsConstructor
        public static class Edge<V> {
            public final Node<V> node1;
            public final Node<V> node2;
            public final V value;
        }

        /**
         * Узел графа
         */
        @EqualsAndHashCode
        @AllArgsConstructor
        public static class Node<V> {
            public final V name;
        }

        private final List<Edge<V>> edges;
        private final List<Node<V>> nodes;

        public Graph(List<Edge<V>> edges) {
            this.edges = edges;
            this.nodes = edges.stream()
                    .flatMap(edge -> Stream.of(edge.node1, edge.node2))
                    .distinct()
                    .toList();
        }

        public Set<Edge<V>> getAllEdgesForCurrentNode(Node<V> node) {
            return edges.stream()
                    .filter(edge -> node.equals(edge.node1) || node.equals(edge.node2))
                    .collect(Collectors.toSet());
        }

        public Set<Node<V>> getNeighborsForCurrentNode(Node<V> node) {
            return getAllEdgesForCurrentNode(node)
                    .stream()
                    .flatMap(edge -> Stream.of(edge.node1, edge.node2))
                    .filter(n -> !n.equals(node))
                    .collect(Collectors.toSet());
        }

        public int getNumberOfNodes() {
            return nodes.size();
        }

        /**
         * @return итератор для прохождения по узлам
         */
        @Override
        public Iterator<Node<V>> iterator() {
            return nodes.iterator();
        }
    }

    protected final String dbName;

    /**
     * @param configurationDTO вся информация о настройках работы микросервисов (consumer, processor, producer)
     * @return словарь, где ключ - название топика (его алиас), а значение - запрос на вставку в БД для его данных
     */
    @Override
    public Map<String, String> createSelectRequestsForEachTopicInConfiguration(ConfigurationDTO configurationDTO) {
        Map<String, String> tableNameAndItsTopicAlias = getMapOfTableNameAndItsTopicAlias(configurationDTO);
        Map<String, List<String>> tableNameAndItsFieldsThatAreInTriggerCondition = getMapOfTableNameAndItsFieldsThatAreInTriggerCondition(configurationDTO);

        Map<String, String> topicAndItsSelectRequest = new HashMap<>();
        Graph<String> graph = createGraphFromTriggerCondition(configurationDTO);
        for (Graph.Node<String> tableToStartBypass : graph) {
            StringBuilder selectRequestStringBuilder = new StringBuilder();
            Set<String> tablesNamesThatAreAlreadyJoined = new HashSet<>();
            Set<Graph.Edge<String>> conditionsThatAreAlreadyInRequest = new HashSet<>();
            Deque<Graph.Node<String>> stackForTablesThatAreNotJoined = new ArrayDeque<>();

            tablesNamesThatAreAlreadyJoined.add(tableToStartBypass.name);

            stackForTablesThatAreNotJoined.addAll(graph.getNeighborsForCurrentNode(tableToStartBypass));
            while (!stackForTablesThatAreNotJoined.isEmpty()) {
                Graph.Node<String> tableToJoin = stackForTablesThatAreNotJoined.pollFirst();

                selectRequestStringBuilder.append(" join %s.%s on ".formatted(dbName, tableToJoin.name));
                tablesNamesThatAreAlreadyJoined.add(tableToJoin.name);

                List<Graph.Edge<String>> conditionsNeedToAddInRequest = graph.getAllEdgesForCurrentNode(tableToJoin)
                        .stream()
                        .filter(condition -> !conditionsThatAreAlreadyInRequest.contains(condition))
                        .filter(condition -> tablesNamesThatAreAlreadyJoined.contains(condition.node1.name) && tablesNamesThatAreAlreadyJoined.contains(condition.node2.name))
                        .toList();
                selectRequestStringBuilder.append(
                        conditionsNeedToAddInRequest.stream().map(condition -> condition.value).collect(Collectors.joining(" and "))
                );
                conditionsThatAreAlreadyInRequest.addAll(conditionsNeedToAddInRequest);

                List<Graph.Node<String>> notVisitedTables = graph.getNeighborsForCurrentNode(tableToJoin)
                        .stream()
                        .filter(table -> !tablesNamesThatAreAlreadyJoined.contains(table.name))
                        .toList();
                stackForTablesThatAreNotJoined.addAll(notVisitedTables);
            }

            String concatenatedValuesToReturn = configurationDTO.getInputKafkaInfo()
                    .getInputTopics()
                    .stream()
                    .filter(inputTopic -> tablesNamesThatAreAlreadyJoined.contains(inputTopic.getTableName()))
                    .flatMap(inputTopic -> inputTopic.getTopicFields()
                            .stream()
                            .filter(TopicFieldDTO::getNeedToReturn)
                            .map(topicField -> inputTopic.getTableName() + DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD.delimiterValue + topicField.getFieldName()))
                    .sorted()
                    .collect(Collectors.joining(", "));
            selectRequestStringBuilder.insert(0, "select %s from %s.%s".formatted(concatenatedValuesToReturn, dbName,tableToStartBypass.name));

            selectRequestStringBuilder.append(" where ");
            String whereCondition = graph.getAllEdgesForCurrentNode(tableToStartBypass)
                    .stream()
                    .filter(condition -> !conditionsThatAreAlreadyInRequest.contains(condition))
                    .map(condition -> condition.value)
                    .collect(Collectors.joining(" and "));

            List<String> fieldsThatAreRequiredToBeInWhereCondition = tableNameAndItsFieldsThatAreInTriggerCondition.get(tableToStartBypass.name);
            if (!whereCondition.isBlank()) whereCondition += " and ";
            whereCondition += fieldsThatAreRequiredToBeInWhereCondition.stream()
                    .map(field -> {
                        String tableNameAndItsField = tableToStartBypass.name + DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD.delimiterValue + field;
                        return tableNameAndItsField + " = ?";
                    })
                    .distinct()
                    .collect(Collectors.joining(" and "));
            selectRequestStringBuilder.append(whereCondition);

            topicAndItsSelectRequest.put(
                    tableNameAndItsTopicAlias.get(tableToStartBypass.name),
                    selectRequestStringBuilder.toString()
            );
        }
        return topicAndItsSelectRequest;
    }

    /**
     * @param configurationDTO вся информация о настройках работы микросервисов (consumer, processor, producer)
     * @return словарь, где ключ - имя таблицы в БД, а значение - соответствующий этой таблице псевдоним топика
     */
    private Map<String, String> getMapOfTableNameAndItsTopicAlias(ConfigurationDTO configurationDTO) {
        Map<String, String> tableNameAndItsTopicAlias = new HashMap<>();
        for (InputTopicDTO inputTopicDTO : configurationDTO.getInputKafkaInfo().getInputTopics()) {
            tableNameAndItsTopicAlias.put(inputTopicDTO.getTableName(), inputTopicDTO.getAlias());
        }
        return tableNameAndItsTopicAlias;
    }

    /**
     * @param configurationDTO вся информация о настройках работы микросервисов (consumer, processor, producer)
     * @return словарь, где ключ - имя таблицы в БД, а значение - список полей, которые указаны в условии для создания триггера
     */
    private Map<String, List<String>> getMapOfTableNameAndItsFieldsThatAreInTriggerCondition(ConfigurationDTO configurationDTO) {
        String triggerCondition = replaceTriggerConditionAliasesToDBTablesNames(configurationDTO.getTriggerCondition(), configurationDTO.getInputKafkaInfo().getInputTopics());

        String conditionForOperatorSplit = "(" + String.join("|", TriggerConditionOperator.getAllOperatorNames()) + ")";
        String conditionForSignSplit = "(" + String.join("|", TriggerConditionSign.getAllSignNames()) + ")";

        Map<String, List<String>> tableNameAndItsFieldsThatAreInTriggerCondition = new HashMap<>();
        Arrays
                .stream(triggerCondition.split(conditionForOperatorSplit))
                .map(String::trim)
                .flatMap(twoConcatenatedTableNameWithFieldAndSign -> Arrays.stream(twoConcatenatedTableNameWithFieldAndSign.split(conditionForSignSplit)))
                .map(twoConcatenatedTableNameWithFieldAndSign -> twoConcatenatedTableNameWithFieldAndSign.replaceAll("\\s+", " "))
                .map(String::trim)
                .filter(concatenatedTableNameWithField -> !concatenatedTableNameWithField.isBlank())
                .forEach(concatenatedTableNameWithField -> {
                    int indexOfDelimiter = concatenatedTableNameWithField.indexOf(DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD.delimiterValue);
                    String tableName = concatenatedTableNameWithField.substring(0, indexOfDelimiter).trim();
                    String field = concatenatedTableNameWithField.substring(indexOfDelimiter + 1).trim();

                    tableNameAndItsFieldsThatAreInTriggerCondition.merge(
                            tableName,
                            Collections.singletonList(field),
                            (fieldList1, fieldList2) -> Stream.of(fieldList1, fieldList2).flatMap(List::stream).toList()
                    );
                });
        return tableNameAndItsFieldsThatAreInTriggerCondition;
    }

    /**
     * @param configurationDTO вся информация о настройках работы микросервисов (consumer, processor, producer)
     * @return возвращает граф, где узел - назавание таблицы, ребро - выражение типа: имяТаблицы1.полеТаблицы <знак> имяТаблицы2.поляТаблицы
     */
    private Graph<String> createGraphFromTriggerCondition(ConfigurationDTO configurationDTO) {
        String triggerCondition = replaceTriggerConditionAliasesToDBTablesNames(configurationDTO.getTriggerCondition(), configurationDTO.getInputKafkaInfo().getInputTopics());

        String conditionForOperatorSplit = "(" + String.join("|", TriggerConditionOperator.getAllOperatorNames()) + ")";
        String conditionForSignSplit = "(" + String.join("|", TriggerConditionSign.getAllSignNames()) + ")";

        List<Graph.Edge<String>> edgesForGraphFromTriggerCondition = Arrays
                .stream(triggerCondition.split(conditionForOperatorSplit))
                .map(String::trim)
                .map(twoConcatenatedTableNameWithFieldAndSign -> twoConcatenatedTableNameWithFieldAndSign.replaceAll("\\s+", " "))
                .map(twoConcatenatedTableNameWithFieldAndSign -> {
                    String firstConcatenatedTableNameWithField = twoConcatenatedTableNameWithFieldAndSign.split(conditionForSignSplit)[0].trim();
                    String secondConcatenatedTableNameWithField = twoConcatenatedTableNameWithFieldAndSign.split(conditionForSignSplit)[1].trim();

                    int indexOfDelimiterForFirstTableName = firstConcatenatedTableNameWithField.indexOf(DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD.delimiterValue);
                    String firstTableName = firstConcatenatedTableNameWithField.substring(0, indexOfDelimiterForFirstTableName).trim();

                    int indexOfDelimiterForSecondTableName = secondConcatenatedTableNameWithField.indexOf(DELIMITER_BETWEEN_TOPIC_ALIAS_OR_TABLE_NAME_AND_FIELD.delimiterValue);
                    String secondTableName = secondConcatenatedTableNameWithField.substring(0, indexOfDelimiterForSecondTableName).trim();

                    Graph.Node<String> node1 = new Graph.Node<>(firstTableName);
                    Graph.Node<String> node2 = new Graph.Node<>(secondTableName);
                    return new Graph.Edge<>(node1, node2, twoConcatenatedTableNameWithFieldAndSign);
                })
                .toList();
        return new Graph<>(edgesForGraphFromTriggerCondition);
    }

    /**
     * @param triggerCondition условие для создания триггера
     * @param inputTopics      вся информация о топиках Кафки для чтения данных
     * @return triggerCondition, но на место alias топиков будут вставлены имена таблиц в БД
     */
    private String replaceTriggerConditionAliasesToDBTablesNames(String triggerCondition, Set<InputTopicDTO> inputTopics) {
        for (InputTopicDTO inputTopic : inputTopics) {
            triggerCondition = triggerCondition.replace(inputTopic.getAlias(), inputTopic.getTableName());
        }
        return triggerCondition;
    }
}
