# Minecraft Server Manager

Javaで構築された軽量なMinecraftサーバー自動管理・運用ツールです。  
プレイヤーの入退室を自動検知し、0人になった際にプロセスを一時停止（サスペンド）することで、ホストPCのCPU・メモリ負荷を大幅に削減します。

---

##  主な機能

- **ASCII Banner & Console HUD**: 起動時に接続IP、ポート、Ping、現在スレッド数、起動時間を水色で中央揃え表示。
- **Auto-Suspend / Resume (SToper)**: 
  - プレイヤー数が0人になると、自動的にMinecraftサーバープロセスを一時停止（CPU使用率0%）。
  - プレイヤーが接続すると、瞬時にプロセスを自動再開。
- **CLI & Command Bridge**: ツール側のコンソールからMinecraftサーバーへ直接コマンドを送信可能。
- **Status Web API**: `http://localhost:8080/status` でサーバー稼働状態や接続情報をJSON形式で取得可能。
- **EULA Auto-Agreement**: 起動時に `eula.txt` を検知し、未存在の場合は自動生成・同意処理。

---

## 📂 フォルダ構成

```text
minecraft_server/
├── Main.java             # メインエントリポイント / UI描画
├── Game.java             # マイクラプロセス管理 / ログ監視
├── Cli.java              # CLI入力受付
├── WebSOcket.java        # Web APIサーバー (Status)
├── SToper.java           # プロセス一時停止・再開制御
├── LICENSE               # MIT License
├── README.md             # ドキュメント
└── Minecraft/
    ├── Port.java         # ポート情報クラス
    ├── Host.java         # ホスト・Ping計測クラス
    ├── IPAddress.java    # IPアドレス解決クラス
    └── Code/
        └── server.jar    # Minecraftサーバー本体 (要配置)
