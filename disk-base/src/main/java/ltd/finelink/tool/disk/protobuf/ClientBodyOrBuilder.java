// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: resources/proto/client_message.proto

package ltd.finelink.tool.disk.protobuf;

public interface ClientBodyOrBuilder extends
    // @@protoc_insertion_point(interface_extends:protobuf.ClientBody)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 type = 1;</code>
   * @return The type.
   */
  int getType();

  /**
   * <code>optional string sub = 2;</code>
   * @return Whether the sub field is set.
   */
  boolean hasSub();
  /**
   * <code>optional string sub = 2;</code>
   * @return The sub.
   */
  java.lang.String getSub();
  /**
   * <code>optional string sub = 2;</code>
   * @return The bytes for sub.
   */
  com.google.protobuf.ByteString
      getSubBytes();

  /**
   * <code>optional string toId = 3;</code>
   * @return Whether the toId field is set.
   */
  boolean hasToId();
  /**
   * <code>optional string toId = 3;</code>
   * @return The toId.
   */
  java.lang.String getToId();
  /**
   * <code>optional string toId = 3;</code>
   * @return The bytes for toId.
   */
  com.google.protobuf.ByteString
      getToIdBytes();

  /**
   * <code>optional string sid = 4;</code>
   * @return Whether the sid field is set.
   */
  boolean hasSid();
  /**
   * <code>optional string sid = 4;</code>
   * @return The sid.
   */
  java.lang.String getSid();
  /**
   * <code>optional string sid = 4;</code>
   * @return The bytes for sid.
   */
  com.google.protobuf.ByteString
      getSidBytes();

  /**
   * <code>optional string msgType = 5;</code>
   * @return Whether the msgType field is set.
   */
  boolean hasMsgType();
  /**
   * <code>optional string msgType = 5;</code>
   * @return The msgType.
   */
  java.lang.String getMsgType();
  /**
   * <code>optional string msgType = 5;</code>
   * @return The bytes for msgType.
   */
  com.google.protobuf.ByteString
      getMsgTypeBytes();

  /**
   * <code>optional bytes content = 6;</code>
   * @return Whether the content field is set.
   */
  boolean hasContent();
  /**
   * <code>optional bytes content = 6;</code>
   * @return The content.
   */
  com.google.protobuf.ByteString getContent();
}
