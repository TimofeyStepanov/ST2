// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: producer.proto

package ru.stepanoff.grpc.producer;

public interface ProducerDTOOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ProducerDTO)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated string messagesForUser = 1;</code>
   * @return A list containing the messagesForUser.
   */
  java.util.List<java.lang.String>
      getMessagesForUserList();
  /**
   * <code>repeated string messagesForUser = 1;</code>
   * @return The count of messagesForUser.
   */
  int getMessagesForUserCount();
  /**
   * <code>repeated string messagesForUser = 1;</code>
   * @param index The index of the element to return.
   * @return The messagesForUser at the given index.
   */
  java.lang.String getMessagesForUser(int index);
  /**
   * <code>repeated string messagesForUser = 1;</code>
   * @param index The index of the value to return.
   * @return The bytes of the messagesForUser at the given index.
   */
  com.google.protobuf.ByteString
      getMessagesForUserBytes(int index);
}
