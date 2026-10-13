
独立游戏社区 App
作者：SuwaBananako
一个面向独立游戏生态的综合性 Android 社区应用，为独立开发者、美术、音乐人以及玩家提供内容创作、游戏宣发、众筹支持与作品投票的一站式平台。
<img width="1080" height="2400" alt="1782307414234" src="https://github.com/user-attachments/assets/4cf93468-1335-43c7-af05-be12c44c6194" />
<img width="1080" height="2400" alt="1782307414222" src="https://github.com/user-attachments/assets/a65a2cee-4c8e-4e3a-a872-99bf754a8822" />
<img width="1080" height="2400" alt="1782307414199" src="https://github.com/user-attachments/assets/1bfccb6a-e610-496c-9457-54732a728865" />
<img width="1080" height="2400" alt="1782307414188" src="https://github.com/user-attachments/assets/19a4a504-883b-4c0d-9f74-56bac6f09745" />
<img width="1080" height="2400" alt="1782307414093" src="https://github.com/user-attachments/assets/6b55645d-2cea-4743-a5f2-dc08c96b2ed8" />
<img width="1080" height="2400" alt="1782307414040" src="https://github.com/user-attachments/assets/53ef9557-3eba-4f32-bc61-004af4318eb5" />
功能
用户管理：注册、登录、自动登录、修改个人资料、退出登录

论坛：帖子列表、搜索、发布（八大分类，支持封面图和内容图）、详情、点赞、收藏、分享、评论、编辑、删除

分类板块：首页 Banner 自动轮播、八大分类入口、分类详情页

游戏宣发：宣发列表、发布宣发、详情页、跳转下载链接

众筹：项目列表、发起项目、详情页、支持众筹（微信 / 支付宝模拟支付）

投票：投票榜单、添加游戏、投票（每人每游戏限一票）、游戏详情

个人中心：资料展示、统计信息、我的帖子 / 游戏 / 宣发 / 众筹 / 收藏、审核通知

管理员后台：内容审核、帖子管理、评论管理、用户管理、游戏内容管理

技术栈
语言：Java

架构：Android MVC

界面：Activity + Fragment + RecyclerView

存储：SQLite（community.db / users.db / profile_[username].db）+ SharedPreferences

图片处理：BitmapFactory、Base64 编码

最低支持：Android 7.0（API 24）

数据库
数据库	说明
community.db	帖子、评论、游戏宣发、众筹、投票、通知
users.db	用户账户信息
profile_[username].db	每个用户的收藏、点赞、投票、支持记录
默认管理员账号：admin / admin123

运行
克隆仓库

bash
git clone https://github.com/SuwaBananako/suwako1.git
用 Android Studio 打开项目，等待 Gradle 同步完成

连接设备或启动模拟器，点击 Run

目录结构
text
com.example.indiegamecommunity
├── activity/      # 各 Activity
├── fragment/      # 用户端 Fragment
├── fragment/admin # 管理员 Fragment
├── model/         # 数据模型类
├── database/      # 数据库帮助类
└── view/          # 自定义 View
后续改进
引入网络通信模块，实现多用户数据云端同步

增加消息推送，实时通知审核结果

优化图片加载性能，引入缓存机制


增加富文本编辑器

接入真实的微信 / 支付宝 SDK
