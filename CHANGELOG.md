
[こちら](https://github.com/primecloud-controller-org/primecloud-controller-build)のプロジェクトを使い、ビルドしたものを[公開](http://www.primecloud-controller.org/download.html)
しています。


2016-06-16 PrimeCloud Controller 2.6.1リリース
============================================

## 主な変更点

hook機能の追加

PCCの下記の制御処理に独自の機能を簡単にフックできる機能を追加しました。(#56)

* myCloud : 作成後、更新前後、削除前後
* サーバ : 作成後、更新前後、削除前後、開始前後、停止前後
* サービス : 作成後、更新前後、削除前後、開始前後、停止前後
* ロードバランサ : 作成後、更新前後、削除前後、開始前後、停止前後
* サーバ協調設定 : 開始前後、停止前後

現時点では、外部スクリプトでフックできる機能を実装してあります。
詳細については、[こちら](http://www.primecloud-controller.jp/documentation/reference/hook/about.html)
を確認してください。


### ELB利用時のSSLプロコトルリスナーをサポート
ELBでSSLプロトコルのリスナーと任意のポートを利用できるようにしました。(#55)

### 既知の不具合の修正
* issues #56 :インストールスクリプト内のbugを修正
* issues #58 :作成したインスタンスに`/root/script`が存在しない場合の処理を改善。



2016-03-30 PrimeCloud Controller 2.6.0リリース
============================================

## 主な変更点

### PCC APIの変更

PCC APIの以下の点を変更しました。

* 主な変更点
    * 不要なパラメータを削除
    * 署名の作成ロジックをWebコンテキスト名を含めないように変更
    * 新規API追加
        * AddAddress, EditAddress, DeleteAddress, ListAddress
        * ListComponentType, GetAttachableComponent
        * DescribePlatform
    * List系APIで返す要素が1つと複数の場合とでレスポンスのJSON形式が異なる問題を修正

* 現行仕様における注意点
    * EditInstanceのIpAddressパラメータで指定した値は、インスタンスを開始するまでDescribeInstanceで表示されない
    * CreateLoadBalancerListenerでリスナーを作成した直後にDeleteLoadBalancerで表示されるStatusは、nullとなる。
    * サービス(component)に割り当てたディスクサイズをAPIで取得することが出来ない

### ロードバランサ機能追加

AWS ELBにてSSLリスナーを使用できるようになりました。

### 既知の不具合



2015-12-16 PrimeCloud Controller 2.5.1リリース
============================================

2.5.0での既知の不具合を修正しました。

## 主な変更点

### maven release pluginの導入
maven release プラグインを利用したリリースを行うように変更しました。

### pccコマンドを、pccadminコマンドに名称変更
管理者向けコマンドラインツールの名称を変更しました。
今後開発予定であるPCC利用者向けコマンドラインツールとの区別のためです。
機能に関しては変更ありません。

### 既知の不具合の修正

* issues #50 :インスタンスからの外部の名前解決が出来ない
* issues #46 :mysql用のマニフェストで利用しているスクリプトのpath


2015-10-03 PrimeCloud Controller 2.5.0リリース
============================================

## 主な新機能

### Zabbix Server 2.2のサポート

連携先の監視システムとしてて、Zabbix Server 2.2をサポート

Zabbix Server 2.2対応の実装には、下記の方からの提供頂いたコードを参考とさせて頂き、
現在のPCCで実装されている機能と統合しています。ありがとうございました。

浅倉氏  
鈴木氏  


### 既知の不具合の修正

* issues #34 :サービス用のDISKが追加できない。
* issues #36 :zabbix 1.8用のテンプレートに設定されているトリガーの条件式の変更
* issues #31 :インストールスクリプト内でのサンプルデータの投入時にSQLの投入順番が正しくない
* issues #41 :DBセットアップに必要なddlが存在しない問題




2015-05-29 PrimeCloud Controller 2.4.5リリース
==============================================

主な新機能
----------

### Microsoft Azureのサポート

Microsoft AzureをPCCのプラットフォームとして追加ができるようになり、Azure上のインスタンス制御が可能となりました。

### OpenStack(Havana)のサポート

OpenStack Havanaに対してのインスタンス制御が可能となりました。

### ニフティクラウド(REST-API)のサポート

NiftyCloudのSOAP-APIの停止に伴い、REST-APIへの変更に対応しました。

### AWS internal ELBのサポート

AWS Elastic Load Balancerの機能を拡張し、Internal ELB機能の利用が可能となりました。

### 管理者向けコマンドラインツールの拡充

従来、主にユーザ管理用として存在したmanagement-toolsに加え、プラットフォーム、イメージ、サービス管理用のコマンドラインツールを実装しました。

### 外部リポジトリからイメージ・サービスの追加機能を追加

PCC用リポジトリの提供を開始しました。
そのリポジトリをコマンドラインから利用するためのコマンドを実装しました。

### その他、多くのバグを修正

