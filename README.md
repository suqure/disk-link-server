
### 
### 
本项目为[disk-link-vue]([suqure/disk-link-vue: 基于WEBRTC的文件传输工具VUE前端,主要包含功能P2P文件传输，视频会议，视频录制 (github.com)](https://github.com/suqure/disk-link-vue))后台代码以及桌面版代码，基于spring boot ,netty ,javafx框架

项目生产体验地址：http://disk.finelink.ltd

代码目录说明

```css
├─ disk-base #基础pojo以及工具类
├─ disk-client #桌面端客户端底层实现
├─ disk-dao #数据库dao层 sqlit
├─ disk-desktop #桌面端APP页面逻辑 JAVAFX实现
├─ disk-server #后台服务端API spring boot + netty

```



环境要求：

JDK8

maven2



## 项目构建

```sh
mvm clean install
```

### 开发环境构建

```sh
mvn clean install -Pdev
```

### 构建生产环境

```sh
mvn clean install -Ppro
```

