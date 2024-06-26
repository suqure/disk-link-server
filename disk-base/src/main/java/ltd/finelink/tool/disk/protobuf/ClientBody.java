// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: resources/proto/client_message.proto

package ltd.finelink.tool.disk.protobuf;

/**
 * Protobuf type {@code protobuf.ClientBody}
 */
public final class ClientBody extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:protobuf.ClientBody)
    ClientBodyOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ClientBody.newBuilder() to construct.
  private ClientBody(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ClientBody() {
    sub_ = "";
    toId_ = "";
    sid_ = "";
    msgType_ = "";
    content_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new ClientBody();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ClientBody(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            type_ = input.readInt32();
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000001;
            sub_ = s;
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000002;
            toId_ = s;
            break;
          }
          case 34: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000004;
            sid_ = s;
            break;
          }
          case 42: {
            java.lang.String s = input.readStringRequireUtf8();
            bitField0_ |= 0x00000008;
            msgType_ = s;
            break;
          }
          case 50: {
            bitField0_ |= 0x00000010;
            content_ = input.readBytes();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (com.google.protobuf.UninitializedMessageException e) {
      throw e.asInvalidProtocolBufferException().setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return ltd.finelink.tool.disk.protobuf.ClientProto.internal_static_protobuf_ClientBody_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return ltd.finelink.tool.disk.protobuf.ClientProto.internal_static_protobuf_ClientBody_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            ltd.finelink.tool.disk.protobuf.ClientBody.class, ltd.finelink.tool.disk.protobuf.ClientBody.Builder.class);
  }

  private int bitField0_;
  public static final int TYPE_FIELD_NUMBER = 1;
  private int type_;
  /**
   * <code>int32 type = 1;</code>
   * @return The type.
   */
  @java.lang.Override
  public int getType() {
    return type_;
  }

  public static final int SUB_FIELD_NUMBER = 2;
  private volatile java.lang.Object sub_;
  /**
   * <code>optional string sub = 2;</code>
   * @return Whether the sub field is set.
   */
  @java.lang.Override
  public boolean hasSub() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>optional string sub = 2;</code>
   * @return The sub.
   */
  @java.lang.Override
  public java.lang.String getSub() {
    java.lang.Object ref = sub_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      sub_ = s;
      return s;
    }
  }
  /**
   * <code>optional string sub = 2;</code>
   * @return The bytes for sub.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getSubBytes() {
    java.lang.Object ref = sub_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      sub_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int TOID_FIELD_NUMBER = 3;
  private volatile java.lang.Object toId_;
  /**
   * <code>optional string toId = 3;</code>
   * @return Whether the toId field is set.
   */
  @java.lang.Override
  public boolean hasToId() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   * <code>optional string toId = 3;</code>
   * @return The toId.
   */
  @java.lang.Override
  public java.lang.String getToId() {
    java.lang.Object ref = toId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      toId_ = s;
      return s;
    }
  }
  /**
   * <code>optional string toId = 3;</code>
   * @return The bytes for toId.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getToIdBytes() {
    java.lang.Object ref = toId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      toId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int SID_FIELD_NUMBER = 4;
  private volatile java.lang.Object sid_;
  /**
   * <code>optional string sid = 4;</code>
   * @return Whether the sid field is set.
   */
  @java.lang.Override
  public boolean hasSid() {
    return ((bitField0_ & 0x00000004) != 0);
  }
  /**
   * <code>optional string sid = 4;</code>
   * @return The sid.
   */
  @java.lang.Override
  public java.lang.String getSid() {
    java.lang.Object ref = sid_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      sid_ = s;
      return s;
    }
  }
  /**
   * <code>optional string sid = 4;</code>
   * @return The bytes for sid.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getSidBytes() {
    java.lang.Object ref = sid_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      sid_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int MSGTYPE_FIELD_NUMBER = 5;
  private volatile java.lang.Object msgType_;
  /**
   * <code>optional string msgType = 5;</code>
   * @return Whether the msgType field is set.
   */
  @java.lang.Override
  public boolean hasMsgType() {
    return ((bitField0_ & 0x00000008) != 0);
  }
  /**
   * <code>optional string msgType = 5;</code>
   * @return The msgType.
   */
  @java.lang.Override
  public java.lang.String getMsgType() {
    java.lang.Object ref = msgType_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      msgType_ = s;
      return s;
    }
  }
  /**
   * <code>optional string msgType = 5;</code>
   * @return The bytes for msgType.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getMsgTypeBytes() {
    java.lang.Object ref = msgType_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      msgType_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int CONTENT_FIELD_NUMBER = 6;
  private com.google.protobuf.ByteString content_;
  /**
   * <code>optional bytes content = 6;</code>
   * @return Whether the content field is set.
   */
  @java.lang.Override
  public boolean hasContent() {
    return ((bitField0_ & 0x00000010) != 0);
  }
  /**
   * <code>optional bytes content = 6;</code>
   * @return The content.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getContent() {
    return content_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (type_ != 0) {
      output.writeInt32(1, type_);
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, sub_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, toId_);
    }
    if (((bitField0_ & 0x00000004) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 4, sid_);
    }
    if (((bitField0_ & 0x00000008) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 5, msgType_);
    }
    if (((bitField0_ & 0x00000010) != 0)) {
      output.writeBytes(6, content_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (type_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, type_);
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, sub_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, toId_);
    }
    if (((bitField0_ & 0x00000004) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, sid_);
    }
    if (((bitField0_ & 0x00000008) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, msgType_);
    }
    if (((bitField0_ & 0x00000010) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(6, content_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof ltd.finelink.tool.disk.protobuf.ClientBody)) {
      return super.equals(obj);
    }
    ltd.finelink.tool.disk.protobuf.ClientBody other = (ltd.finelink.tool.disk.protobuf.ClientBody) obj;

    if (getType()
        != other.getType()) return false;
    if (hasSub() != other.hasSub()) return false;
    if (hasSub()) {
      if (!getSub()
          .equals(other.getSub())) return false;
    }
    if (hasToId() != other.hasToId()) return false;
    if (hasToId()) {
      if (!getToId()
          .equals(other.getToId())) return false;
    }
    if (hasSid() != other.hasSid()) return false;
    if (hasSid()) {
      if (!getSid()
          .equals(other.getSid())) return false;
    }
    if (hasMsgType() != other.hasMsgType()) return false;
    if (hasMsgType()) {
      if (!getMsgType()
          .equals(other.getMsgType())) return false;
    }
    if (hasContent() != other.hasContent()) return false;
    if (hasContent()) {
      if (!getContent()
          .equals(other.getContent())) return false;
    }
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + TYPE_FIELD_NUMBER;
    hash = (53 * hash) + getType();
    if (hasSub()) {
      hash = (37 * hash) + SUB_FIELD_NUMBER;
      hash = (53 * hash) + getSub().hashCode();
    }
    if (hasToId()) {
      hash = (37 * hash) + TOID_FIELD_NUMBER;
      hash = (53 * hash) + getToId().hashCode();
    }
    if (hasSid()) {
      hash = (37 * hash) + SID_FIELD_NUMBER;
      hash = (53 * hash) + getSid().hashCode();
    }
    if (hasMsgType()) {
      hash = (37 * hash) + MSGTYPE_FIELD_NUMBER;
      hash = (53 * hash) + getMsgType().hashCode();
    }
    if (hasContent()) {
      hash = (37 * hash) + CONTENT_FIELD_NUMBER;
      hash = (53 * hash) + getContent().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static ltd.finelink.tool.disk.protobuf.ClientBody parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ltd.finelink.tool.disk.protobuf.ClientBody parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ltd.finelink.tool.disk.protobuf.ClientBody parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ltd.finelink.tool.disk.protobuf.ClientBody parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ltd.finelink.tool.disk.protobuf.ClientBody parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ltd.finelink.tool.disk.protobuf.ClientBody parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ltd.finelink.tool.disk.protobuf.ClientBody parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ltd.finelink.tool.disk.protobuf.ClientBody parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static ltd.finelink.tool.disk.protobuf.ClientBody parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static ltd.finelink.tool.disk.protobuf.ClientBody parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static ltd.finelink.tool.disk.protobuf.ClientBody parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ltd.finelink.tool.disk.protobuf.ClientBody parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(ltd.finelink.tool.disk.protobuf.ClientBody prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code protobuf.ClientBody}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:protobuf.ClientBody)
      ltd.finelink.tool.disk.protobuf.ClientBodyOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ltd.finelink.tool.disk.protobuf.ClientProto.internal_static_protobuf_ClientBody_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ltd.finelink.tool.disk.protobuf.ClientProto.internal_static_protobuf_ClientBody_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ltd.finelink.tool.disk.protobuf.ClientBody.class, ltd.finelink.tool.disk.protobuf.ClientBody.Builder.class);
    }

    // Construct using ltd.finelink.tool.disk.protobuf.ClientBody.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      type_ = 0;

      sub_ = "";
      bitField0_ = (bitField0_ & ~0x00000001);
      toId_ = "";
      bitField0_ = (bitField0_ & ~0x00000002);
      sid_ = "";
      bitField0_ = (bitField0_ & ~0x00000004);
      msgType_ = "";
      bitField0_ = (bitField0_ & ~0x00000008);
      content_ = com.google.protobuf.ByteString.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000010);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return ltd.finelink.tool.disk.protobuf.ClientProto.internal_static_protobuf_ClientBody_descriptor;
    }

    @java.lang.Override
    public ltd.finelink.tool.disk.protobuf.ClientBody getDefaultInstanceForType() {
      return ltd.finelink.tool.disk.protobuf.ClientBody.getDefaultInstance();
    }

    @java.lang.Override
    public ltd.finelink.tool.disk.protobuf.ClientBody build() {
      ltd.finelink.tool.disk.protobuf.ClientBody result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public ltd.finelink.tool.disk.protobuf.ClientBody buildPartial() {
      ltd.finelink.tool.disk.protobuf.ClientBody result = new ltd.finelink.tool.disk.protobuf.ClientBody(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      result.type_ = type_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        to_bitField0_ |= 0x00000001;
      }
      result.sub_ = sub_;
      if (((from_bitField0_ & 0x00000002) != 0)) {
        to_bitField0_ |= 0x00000002;
      }
      result.toId_ = toId_;
      if (((from_bitField0_ & 0x00000004) != 0)) {
        to_bitField0_ |= 0x00000004;
      }
      result.sid_ = sid_;
      if (((from_bitField0_ & 0x00000008) != 0)) {
        to_bitField0_ |= 0x00000008;
      }
      result.msgType_ = msgType_;
      if (((from_bitField0_ & 0x00000010) != 0)) {
        to_bitField0_ |= 0x00000010;
      }
      result.content_ = content_;
      result.bitField0_ = to_bitField0_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof ltd.finelink.tool.disk.protobuf.ClientBody) {
        return mergeFrom((ltd.finelink.tool.disk.protobuf.ClientBody)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(ltd.finelink.tool.disk.protobuf.ClientBody other) {
      if (other == ltd.finelink.tool.disk.protobuf.ClientBody.getDefaultInstance()) return this;
      if (other.getType() != 0) {
        setType(other.getType());
      }
      if (other.hasSub()) {
        bitField0_ |= 0x00000001;
        sub_ = other.sub_;
        onChanged();
      }
      if (other.hasToId()) {
        bitField0_ |= 0x00000002;
        toId_ = other.toId_;
        onChanged();
      }
      if (other.hasSid()) {
        bitField0_ |= 0x00000004;
        sid_ = other.sid_;
        onChanged();
      }
      if (other.hasMsgType()) {
        bitField0_ |= 0x00000008;
        msgType_ = other.msgType_;
        onChanged();
      }
      if (other.hasContent()) {
        setContent(other.getContent());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      ltd.finelink.tool.disk.protobuf.ClientBody parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (ltd.finelink.tool.disk.protobuf.ClientBody) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private int type_ ;
    /**
     * <code>int32 type = 1;</code>
     * @return The type.
     */
    @java.lang.Override
    public int getType() {
      return type_;
    }
    /**
     * <code>int32 type = 1;</code>
     * @param value The type to set.
     * @return This builder for chaining.
     */
    public Builder setType(int value) {
      
      type_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 type = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearType() {
      
      type_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object sub_ = "";
    /**
     * <code>optional string sub = 2;</code>
     * @return Whether the sub field is set.
     */
    public boolean hasSub() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional string sub = 2;</code>
     * @return The sub.
     */
    public java.lang.String getSub() {
      java.lang.Object ref = sub_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        sub_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string sub = 2;</code>
     * @return The bytes for sub.
     */
    public com.google.protobuf.ByteString
        getSubBytes() {
      java.lang.Object ref = sub_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        sub_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string sub = 2;</code>
     * @param value The sub to set.
     * @return This builder for chaining.
     */
    public Builder setSub(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
      sub_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string sub = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearSub() {
      bitField0_ = (bitField0_ & ~0x00000001);
      sub_ = getDefaultInstance().getSub();
      onChanged();
      return this;
    }
    /**
     * <code>optional string sub = 2;</code>
     * @param value The bytes for sub to set.
     * @return This builder for chaining.
     */
    public Builder setSubBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000001;
      sub_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object toId_ = "";
    /**
     * <code>optional string toId = 3;</code>
     * @return Whether the toId field is set.
     */
    public boolean hasToId() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional string toId = 3;</code>
     * @return The toId.
     */
    public java.lang.String getToId() {
      java.lang.Object ref = toId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        toId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string toId = 3;</code>
     * @return The bytes for toId.
     */
    public com.google.protobuf.ByteString
        getToIdBytes() {
      java.lang.Object ref = toId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        toId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string toId = 3;</code>
     * @param value The toId to set.
     * @return This builder for chaining.
     */
    public Builder setToId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      toId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string toId = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearToId() {
      bitField0_ = (bitField0_ & ~0x00000002);
      toId_ = getDefaultInstance().getToId();
      onChanged();
      return this;
    }
    /**
     * <code>optional string toId = 3;</code>
     * @param value The bytes for toId to set.
     * @return This builder for chaining.
     */
    public Builder setToIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000002;
      toId_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object sid_ = "";
    /**
     * <code>optional string sid = 4;</code>
     * @return Whether the sid field is set.
     */
    public boolean hasSid() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>optional string sid = 4;</code>
     * @return The sid.
     */
    public java.lang.String getSid() {
      java.lang.Object ref = sid_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        sid_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string sid = 4;</code>
     * @return The bytes for sid.
     */
    public com.google.protobuf.ByteString
        getSidBytes() {
      java.lang.Object ref = sid_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        sid_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string sid = 4;</code>
     * @param value The sid to set.
     * @return This builder for chaining.
     */
    public Builder setSid(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
      sid_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string sid = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearSid() {
      bitField0_ = (bitField0_ & ~0x00000004);
      sid_ = getDefaultInstance().getSid();
      onChanged();
      return this;
    }
    /**
     * <code>optional string sid = 4;</code>
     * @param value The bytes for sid to set.
     * @return This builder for chaining.
     */
    public Builder setSidBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000004;
      sid_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object msgType_ = "";
    /**
     * <code>optional string msgType = 5;</code>
     * @return Whether the msgType field is set.
     */
    public boolean hasMsgType() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <code>optional string msgType = 5;</code>
     * @return The msgType.
     */
    public java.lang.String getMsgType() {
      java.lang.Object ref = msgType_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        msgType_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string msgType = 5;</code>
     * @return The bytes for msgType.
     */
    public com.google.protobuf.ByteString
        getMsgTypeBytes() {
      java.lang.Object ref = msgType_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        msgType_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string msgType = 5;</code>
     * @param value The msgType to set.
     * @return This builder for chaining.
     */
    public Builder setMsgType(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000008;
      msgType_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string msgType = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearMsgType() {
      bitField0_ = (bitField0_ & ~0x00000008);
      msgType_ = getDefaultInstance().getMsgType();
      onChanged();
      return this;
    }
    /**
     * <code>optional string msgType = 5;</code>
     * @param value The bytes for msgType to set.
     * @return This builder for chaining.
     */
    public Builder setMsgTypeBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      bitField0_ |= 0x00000008;
      msgType_ = value;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString content_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>optional bytes content = 6;</code>
     * @return Whether the content field is set.
     */
    @java.lang.Override
    public boolean hasContent() {
      return ((bitField0_ & 0x00000010) != 0);
    }
    /**
     * <code>optional bytes content = 6;</code>
     * @return The content.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getContent() {
      return content_;
    }
    /**
     * <code>optional bytes content = 6;</code>
     * @param value The content to set.
     * @return This builder for chaining.
     */
    public Builder setContent(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000010;
      content_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional bytes content = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearContent() {
      bitField0_ = (bitField0_ & ~0x00000010);
      content_ = getDefaultInstance().getContent();
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:protobuf.ClientBody)
  }

  // @@protoc_insertion_point(class_scope:protobuf.ClientBody)
  private static final ltd.finelink.tool.disk.protobuf.ClientBody DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new ltd.finelink.tool.disk.protobuf.ClientBody();
  }

  public static ltd.finelink.tool.disk.protobuf.ClientBody getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ClientBody>
      PARSER = new com.google.protobuf.AbstractParser<ClientBody>() {
    @java.lang.Override
    public ClientBody parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ClientBody(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ClientBody> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ClientBody> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public ltd.finelink.tool.disk.protobuf.ClientBody getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

