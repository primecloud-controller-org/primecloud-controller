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

ビルド環境
----------

[こちら](https://github.com/primecloud-controller-org/primecloud-controller-build)のプロジェクトを使い、ビルドしたものを[公開](http://www.primecloud-controller.org/download/index.html)
しています。

