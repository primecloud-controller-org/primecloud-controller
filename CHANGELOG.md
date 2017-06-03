
[こちら](https://github.com/primecloud-controller-org/primecloud-controller-build)のプロジェクトを使い、ビルドしたものを[公開](http://www.primecloud-controller.org/download/index.html)
しています。


2017-06-03 PrimeCloud Controller 2.9.0リリース
============================================

## 主な変更点

### 機能追加

- ユーザの最終ログイン日時を記録するようにしました。 [#95](https://github.com/primecloud-controller-org/primecloud-controller/issues/95)
- サーバのルートボリュームサイズの上限を設定できるようにしました。 [#96](https://github.com/primecloud-controller-org/primecloud-controller/issues/96)

### 改善

- VMwareの仮想マシンに複数ネットワークインターフェースを付けるかどうかを指定できるようにしました。 [#77](https://github.com/primecloud-controller-org/primecloud-controller/issues/77)
- サービスに関連付かないボリュームがある場合の動作を改善しました。 [357709d](https://github.com/primecloud-controller-org/primecloud-controller/commit/357709d7817d99ee8e28b473c301f2aaa6aea08e)

### 修正

- 無効にしたユーザがAPIを利用できてしまう不具合を修正しました。 [#93](https://github.com/primecloud-controller-org/primecloud-controller/issues/93)
- VMwareプラットフォームでIPv4アドレスのないネットワークアダプタがあると起動エラーとなる問題を修正しました。 [#97](https://github.com/primecloud-controller-org/primecloud-controller/issues/97)

### 変更

- ユーザ権限機能を除去しました。 [#92](https://github.com/primecloud-controller-org/primecloud-controller/issues/92)
- ユーザが有効か無効どうかのチェックをわかりやすくしました。 [#94](https://github.com/primecloud-controller-org/primecloud-controller/issues/94)

### その他

- Zabbix 3.0.8 および 3.2.4 での動作を確認しました。 [9c18377](https://github.com/primecloud-controller-org/primecloud-controller/commit/9c183779837955c0d66a37bf20d7d16b34c26508)
- ソースコードをリファクタリングしました。


## アップグレード

バージョン2.8.0の環境をアップグレードする場合、次の点に注意してください。

### コンフィグファイルの変更

```/opt/adc/conf/config.properties``` ファイルに次の設定を追加してください。  
（※追加しなくても動作に影響はありません。）

```
aws.maxRootSize = 
vmware.maxRootSize = 
```

### データベースのスキーマの変更

データベースで次のDDL文を実行し、スキーマを変更してください。

```sql
drop table USER_AUTH;
alter table USER drop foreign key USER_FK1;
alter table USER drop MASTER_USER;
alter table USER drop POWER_USER;

alter table USER add ENABLED boolean not null default true after PASSWORD;
update USER set ENABLED = 0, PASSWORD = trim(leading "DISABLE\t" from PASSWORD) where PASSWORD like "DISABLE\t%";

alter table API_CERTIFICATE add ENABLED boolean not null default true after API_SECRET_KEY;

alter table USER add LAST_LOGIN_DATE datetime after ENABLED;
alter table API_CERTIFICATE add LAST_USE_DATE datetime after ENABLED;
```



2017-01-05 PrimeCloud Controller 2.8.0リリース
============================================

## 主な変更点

### 機能追加

- Java 8環境をサポートしました。 [#74](https://github.com/primecloud-controller-org/primecloud-controller/issues/74)
- VMware vSphere 6.0をサポートしました。 [#89](https://github.com/primecloud-controller-org/primecloud-controller/issues/89)
- Zabbix Server 3.0系をサポートしました。 [#76](https://github.com/primecloud-controller-org/primecloud-controller/issues/76)
- AWSプラットフォームのサーバのルートディスクサイズを変更できるようにしました。 [#64](https://github.com/primecloud-controller-org/primecloud-controller/issues/64)
- VMwareプラットフォームのサーバのルートディスクサイズを変更できるようにしました。 [#90](https://github.com/primecloud-controller-org/primecloud-controller/issues/90)
- 実行できるAPIを制限できるようにしました。 [#79](https://github.com/primecloud-controller-org/primecloud-controller/issues/79)

### 改善

- AWSプラットフォームで利用できるサブネットを限定できるようにしました。 [#84](https://github.com/primecloud-controller-org/primecloud-controller/issues/84)
- AWSプラットフォームのサーバでプライベートIPアドレスやElastic IP利用するかどうか変更できるようにしました。 [d2b51a3](https://github.com/primecloud-controller-org/primecloud-controller/commit/d2b51a3ff1a3cf9c712924a39daa0e74a37ed890)
- AWSプラットフォームのサーバで利用できるインスタンスストア数を制限できるようにしました。 [a5b85c9](https://github.com/primecloud-controller-org/primecloud-controller/commit/a5b85c92e8ad40b6b1a489216286db526f258a79)
- AWSプラットフォームのボリュームのタイプを指定できるようにしました。 [#86](https://github.com/primecloud-controller-org/primecloud-controller/issues/86)
- 特別な設定がなくてもクラウドプラットフォームが提供するロードバランサを利用できるようにしました。 [#81](https://github.com/primecloud-controller-org/primecloud-controller/issues/81)
- UltraMonkeyロードバランサを起動した際に、Zabbixのホストとして登録するようにしました。 [#88](https://github.com/primecloud-controller-org/primecloud-controller/issues/88)
- EditInstanceVmware APIが正しく動作するように修正しました。 [37fbce1](https://github.com/primecloud-controller-org/primecloud-controller/commit/37fbce1919fb737c301a59c9d2bca683c7e6df1f)
- EditInstance APIをクラウドプラットフォームごとのAPIに分割しました。 [f1631da](https://github.com/primecloud-controller-org/primecloud-controller/commit/f1631dabfe4a0608c094881ff4daa28b26cf4c70)
- Puppetのマニフェストの中で、同じコンポーネントがアタッチされたインスタンスの情報を扱えるようにしました。 [#82](https://github.com/primecloud-controller-org/primecloud-controller/issues/82)

### 修正

- 認証が必要なプロキシ環境下でクラウドプラットフォームを制御できない不具合を修正しました。 [#78](https://github.com/primecloud-controller-org/primecloud-controller/issues/78)
- プラットフォーム情報の取得APIでシステム内部の情報を取得できてしまう問題を修正しました。 [#80](https://github.com/primecloud-controller-org/primecloud-controller/issues/80)
- AWSロードバランサのリスナーのプロトコルをSSLに変更できない不具合を修正しました。 [#83](https://github.com/primecloud-controller-org/primecloud-controller/issues/83)
- UltraMonkeyロードバランサを停止した際に、WARNレベルのログが出力される問題を修正しました。 [#87](https://github.com/primecloud-controller-org/primecloud-controller/issues/87)
- Windowsサーバのパスワード取得ボタンが使えないプラットフォーム以外でも表示されてしまう問題を修正しました。 [2bc7cba](https://github.com/primecloud-controller-org/primecloud-controller/commit/2bc7cbae8d63a86e5678b85ef6ddc37195ae3481)

### 変更

- サーバ追加画面のサーバ種別欄にOSを表示しないようにしました。 [fb0c19a](https://github.com/primecloud-controller-org/primecloud-controller/commit/fb0c19ae9f8f03601624eb128459b6440d8371a3)
- 一部APIの戻り値のフィールド名を変更しました。 [244d61b](https://github.com/primecloud-controller-org/primecloud-controller/commit/244d61b5788e95d8c13929c2ec1d128084820c83)
- 実験的に実装していたオートスケーリング関連のコードを削除しました。 [1b7d73a](https://github.com/primecloud-controller-org/primecloud-controller/commit/1b7d73a4122b84d8ddf3ec8125c2ae290cc8f3f5)
- UltraMonkeyロードバランサで自動的に付けられるサーバ名のルールを変更しました。 [6717cb8](https://github.com/primecloud-controller-org/primecloud-controller/commit/6717cb804d9ee4a5f5d05138b224923e4871ecc6)

### その他

- インストール時に導入されるサンプルデータを変更しました。 [c2b5b8a](https://github.com/primecloud-controller-org/primecloud-controller/commit/c2b5b8a09695e5d35485607b01cc5b575ae0dd45)
- AWSプラットフォームの制御において、IaaS Gatewayの代わりにaws-java-sdkを用いるように変更しました。 [#72](https://github.com/primecloud-controller-org/primecloud-controller/issues/72)
- ソースコードを全体的にリファクタリングしました。 


## アップグレード

バージョン2.7.0の環境をアップグレードする場合、次の点に注意してください。

### コンフィグファイルの変更

```/opt/adc/conf/config.properties``` ファイルに次の設定を追加してください。  
（※追加しなくても動作に影響はありません。）

```
aws.logging = false
aws.maxInstanceStore = 4
aws.volumeType = standard

ui.aws.enablePrivateIp = true
ui.aws.enableElasticIp = true

api.allowApi = .*
```

### データベースのスキーマの変更

データベースで次のDDL文を実行し、スキーマを変更してください。

```sql
drop table AUTO_SCALING_CONF;

alter table PLATFORM_AWS add column SUBNET_ID varchar(300);

create table ZABBIX_LOAD_BALANCER (
    LOAD_BALANCER_NO bigint not null,
    HOSTID varchar(20),
    STATUS varchar(20),
    constraint ZABBIX_LOAD_BALANCER_PK primary key(LOAD_BALANCER_NO)
);
alter table ZABBIX_LOAD_BALANCER add constraint ZABBIX_LOAD_BALANCER_FK1 foreign key (LOAD_BALANCER_NO) references LOAD_BALANCER (LOAD_BALANCER_NO);
insert into ZABBIX_LOAD_BALANCER select LOAD_BALANCER_NO, HOSTID, STATUS from AWS_LOAD_BALANCER;
alter table AWS_LOAD_BALANCER drop column HOSTID;
alter table AWS_LOAD_BALANCER drop column STATUS;

alter table IMAGE_AWS add column ROOT_SIZE int;
alter table AWS_INSTANCE add column ROOT_SIZE int after SUBNET_ID;

alter table IMAGE_VMWARE add column ROOT_SIZE int;
alter table VMWARE_INSTANCE add column ROOT_SIZE int after KEY_PAIR_NO;
```



2016-09-22 PrimeCloud Controller 2.7.0リリース
============================================

## 主な変更点

### 機能追加

- サーバのイメージごとにPuppetやZabbixを利用しないように設定できるようにしました。 [#61](https://github.com/primecloud-controller-org/primecloud-controller/issues/61)
- 1つのイメージやサービスで複数のZabbixテンプレートを利用できるようにしました。 [#65](https://github.com/primecloud-controller-org/primecloud-controller/issues/65)
- 画面上のサービスViewを非表示にできるようにしました。 [#69](https://github.com/primecloud-controller-org/primecloud-controller/issues/69)
- 画面上におけるプラットフォーム、イメージ、サービス種類の表示順を変更できるようにしました。 [#70](https://github.com/primecloud-controller-org/primecloud-controller/issues/70)

### 改善

- AWSプラットフォームのサーバ作成時に、1つ目のサブネットが自動的に選択されるようにしました。 [#48](https://github.com/primecloud-controller-org/primecloud-controller/issues/48)
- Amazon EC2の長いリソースIDに対応しました。 [#53](https://github.com/primecloud-controller-org/primecloud-controller/issues/53)
- イメージごとのZabbixテンプレートの指定を必須から任意に緩和しました。
- 参照系のいくつかのAPIにおいて、レスポンスに含まれる情報を追加しました。
- データベースのいくつかのテーブルに外部キー制約を追加しました。 [#71](https://github.com/primecloud-controller-org/primecloud-controller/issues/71)

### 修正

- インストールスクリプトにおいて、SQLAlchemyが正しくインストールされない問題を修正しました。 [#59](https://github.com/primecloud-controller-org/primecloud-controller/issues/59)
- AWSプラットフォームのサーバのサブネットを選択すると、それ以降変更できなくなる問題を修正しました。 [#60](https://github.com/primecloud-controller-org/primecloud-controller/issues/60)
- IaaS Gatewayでエラーが発生すると、クラウドの状態とデータベースの間に不整合が生じることがある問題を修正しました。 [#62](https://github.com/primecloud-controller-org/primecloud-controller/issues/62)
- ListAwsAddress APIで別のユーザのリソースを参照できてしまう問題を修正しました。 [#66](https://github.com/primecloud-controller-org/primecloud-controller/issues/66)
- AWSプラットフォームでElastic IPを利用していると、EditInstance APIの実行時にエラーが発生する問題を修正しました。 [#67](https://github.com/primecloud-controller-org/primecloud-controller/issues/67)
- いくつかのAPIで不要なパラメータが必須になっている問題を修正しました。 [#68](https://github.com/primecloud-controller-org/primecloud-controller/issues/68)

### 変更

- 画面上で行える子ユーザの管理機能を無効にしました。

## アップグレード

バージョン2.6.1の環境をアップグレードする場合、次の点に注意してください。

### コンフィグファイルの変更

```/opt/adc/conf/config.properties``` ファイルに次の設定を追加してください。  
（※追加しなくても動作に影響はありません。）

```
ui.enableService = true
```

### データベースのスキーマの変更

データベースで次のDDL文を実行し、スキーマを変更してください。

```sql
alter table AWS_INSTANCE modify INSTANCE_ID varchar(30);
alter table AWS_VOLUME modify INSTANCE_ID varchar(30);
alter table AWS_VOLUME modify VOLUME_ID varchar(30);
alter table AWS_VOLUME modify SNAPSHOT_ID varchar(30);
alter table AWS_ADDRESS modify INSTANCE_ID varchar(30);
alter table AWS_SNAPSHOT modify SNAPSHOT_ID varchar(30);

alter table IMAGE add ZABBIX_DISABLED tinyint(1) not null;
alter table IMAGE add PUPPET_DISABLED tinyint(1) not null;

alter table USER add constraint USER_FK1 foreign key (MASTER_USER) references USER (USER_NO); 
alter table USER_AUTH add constraint USER_AUTH_FK1 foreign key (USER_NO) references USER (USER_NO);

alter table PLATFORM add VIEW_ORDER int(10);
alter table IMAGE add VIEW_ORDER int(10);
alter table COMPONENT_TYPE add VIEW_ORDER int(10);
```

### IaaS Gatewayの変更

配布パッケージに含まれる IaaS Gateway のソースコードのうち、4つのファイルを次のパスにコピーして置き換えてください。

```
cp PrimeCloud-Controller-2.7.0/2.7.0/iaasgw/AllocateAddress.py /opt/adc/iaasgw/AllocateAddress.py
cp PrimeCloud-Controller-2.7.0/2.7.0/iaasgw/ReleaseAddress.py /opt/adc/iaasgw/ReleaseAddress.py
cp PrimeCloud-Controller-2.7.0/2.7.0/iaasgw/iaasgw/controller/ec2/ec2controller.py /opt/adc/iaasgw/iaasgw/controller/ec2/ec2controller.py
cp PrimeCloud-Controller-2.7.0/2.7.0/iaasgw/iaasgw/db/mysqlConnector.py /opt/adc/iaasgw/iaasgw/db/mysqlConnector.py
```


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

