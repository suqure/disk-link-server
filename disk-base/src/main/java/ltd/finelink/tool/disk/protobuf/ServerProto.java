// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: resources/proto/server_message.proto

package ltd.finelink.tool.disk.protobuf;

public final class ServerProto {
  private ServerProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_protobuf_ServerBody_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_protobuf_ServerBody_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_protobuf_ServerMessage_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_protobuf_ServerMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n$resources/proto/server_message.proto\022\010" +
      "protobuf\"\321\002\n\nServerBody\022\014\n\004type\030\001 \001(\005\022\013\n" +
      "\003sid\030\002 \001(\t\022\021\n\004code\030\003 \001(\tH\000\210\001\001\022\024\n\007msgType" +
      "\030\004 \001(\tH\001\210\001\001\022\020\n\003msg\030\005 \001(\tH\002\210\001\001\022\020\n\003cid\030\006 \001" +
      "(\tH\003\210\001\001\022\020\n\003sub\030\007 \001(\tH\004\210\001\001\022\023\n\006fromId\030\010 \001(" +
      "\tH\005\210\001\001\022\021\n\004toId\030\t \001(\tH\006\210\001\001\022\020\n\003gid\030\n \001(\tH\007" +
      "\210\001\001\022\026\n\ttimestamp\030\013 \001(\003H\010\210\001\001\022\024\n\007content\030\014" +
      " \001(\014H\t\210\001\001B\007\n\005_codeB\n\n\010_msgTypeB\006\n\004_msgB\006" +
      "\n\004_cidB\006\n\004_subB\t\n\007_fromIdB\007\n\005_toIdB\006\n\004_g" +
      "idB\014\n\n_timestampB\n\n\010_content\"\370\001\n\rServerM" +
      "essage\0223\n\004type\030\001 \001(\0162%.protobuf.ServerMe" +
      "ssage.ServerMsgType\022\013\n\003biz\030\002 \001(\t\022\024\n\007trac" +
      "eId\030\003 \001(\tH\000\210\001\001\022\021\n\ttimestamp\030\004 \001(\003\022\"\n\004bod" +
      "y\030\005 \003(\0132\024.protobuf.ServerBody\"L\n\rServerM" +
      "sgType\022\n\n\006SYSTEM\020\000\022\010\n\004CHAT\020\001\022\n\n\006CUSTOM\020\002" +
      "\022\r\n\tSUBSCRIBE\020\003\022\n\n\006WEBRTC\020\004B\n\n\010_traceIdB" +
      "0\n\037ltd.finelink.tool.disk.protobufB\013Serv" +
      "erProtoP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_protobuf_ServerBody_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_protobuf_ServerBody_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_protobuf_ServerBody_descriptor,
        new java.lang.String[] { "Type", "Sid", "Code", "MsgType", "Msg", "Cid", "Sub", "FromId", "ToId", "Gid", "Timestamp", "Content", "Code", "MsgType", "Msg", "Cid", "Sub", "FromId", "ToId", "Gid", "Timestamp", "Content", });
    internal_static_protobuf_ServerMessage_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_protobuf_ServerMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_protobuf_ServerMessage_descriptor,
        new java.lang.String[] { "Type", "Biz", "TraceId", "Timestamp", "Body", "TraceId", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}