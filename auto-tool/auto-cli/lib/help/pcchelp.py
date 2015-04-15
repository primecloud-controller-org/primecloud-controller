# -*- coding: utf-8 -*-
import subprocess
import sys

def pccHelp():
    return("概要\n"
            "　　PCCの各種データを操作するためのコマンドです。\n"
            "使い方\n"
            "　　pcc [コマンド] [ターゲット] [オプション]...\n"
            "コマンド一覧\n"
            "<IaaS管理系>\n"
            "　　pcc list iaas [オプション]...　　　　　　 PCCがサポートしているIaaSの一覧表示を行います。\n"
            "　　pcc show iaas [オプション]...　　　　　　 PCCがサポートしているIaaSの詳細情報表示を行います。\n"
            "\n"
            "<プラットフォーム管理系>\n"
            "　　pcc add platform [オプション]...　　　　　各種IaaSプラットフォームの新規追加を行います。\n"
            "　　pcc update platform [オプション]...　　　 登録されたプラットフォーム情報の更新を行います。\n"
            "　　pcc del platform [オプション]...　　　　　登録されたプラットフォーム情報の削除を行います。\n"
            "　　pcc enable platform [オプション]...　　　 指定されたプラットフォームの有効化を行います。\n"
            "　　pcc disable platform [オプション]...　　　指定されたプラットフォームの無効化を行います。\n"
            "　　pcc list platform [オプション]...　　　　 プラットフォームの一覧表示を行います。\n"
            "　　pcc show platform [オプション]...　　　　 指定されたプラットフォームの詳細情報表示を行います。\n"
            "\n"
            "<インスタンスタイプ管理系>\n"
            "　　pcc add instancetype [オプション]...　　　プラットフォームのインスタンスタイプ新規追加を行います。\n"
            "　　pcc update instancetype [オプション]...　 プラットフォームのインスタンスタイプ更新を行います。\n"
            "　　pcc del instancetype [オプション]...　　　プラットフォームのインスタンスタイプ削除を行います。\n"
            "　　pcc list instancetype [オプション]...　　 インスタンスタイプの一覧表示を行います。\n"
            "\n"
            "<ストレージタイプ管理系>\n"
            "　　pcc add storagetype [オプション]...　　　 プラットフォームのストレージタイプ新規追加を行います。\n"
            "　　pcc update storagetype [オプション]...　　プラットフォームのストレージタイプ更新を行います。\n"
            "　　pcc del storagetype [オプション]...　　　 プラットフォームのストレージタイプ削除を行います。\n"
            "　　pcc list storagetype [オプション]...　　　ストレージタイプの一覧表示を行います。\n"
            "\n"
            "<イメージ管理系>\n"
            "　　pcc add image [オプション]...　　　　　　 OSイメージの新規追加を行います。\n"
            "　　pcc update image [オプション]...　　　　　登録されたOSイメージの情報の更新を行います。\n"
            "　　pcc del image [オプション]...　　　　　　 登録されたOSイメージの情報の削除を行います。\n"
            "　　pcc enable image [オプション]...　　　　　指定されたOSイメージの有効化を行います。\n"
            "　　pcc disable image [オプション]...　　　　 指定されたOSイメージの無効化を行います。\n"
            "　　pcc list image [オプション]...　　　　　　OSイメージの一覧表示を行います。\n"
            "　　pcc show image [オプション]...　　　　　　指定されたOSイメージの詳細情報表示を行います。\n"
            "\n"
            "<サービス管理系>\n"
            "　　pcc add service [オプション]...　　　　　 サービスの新規追加を行います。\n"
            "　　pcc update service [オプション]...　　　　登録されたサービスの情報の更新を行います。\n"
            "　　pcc delete service [オプション]...　　　　登録されたサービスの情報の削除を行います。\n"
            "　　pcc enable service [オプション]...　　　　登録されたサービスの有効化を行います。\n"
            "　　pcc disable service [オプション]...　　　 登録されたサービスの無効化を行います。\n"
            "　　pcc list service [オプション]...　　　　　サービスの一覧表示を行います。\n"
            "　　pcc show service [オプション]...　　　　　指定されたサービスの詳細情報表示を行います。\n"
            "　　pcc validate service [オプション]...　　　イメージ上で利用可能なサービス情報の追加を行います。\n"
            "　　pcc revoke service [オプション]...　　　　イメージ上で利用可能なサービス情報の削除を行います。\n")

def addHelp():
    return("概要\n"
            "　　PCCの各種データを追加するためのコマンドです。\n"
            "使い方\n"
            "　　pcc add [ターゲット] [オプション]...\n"
            "コマンド一覧\n"
            "　　pcc add platform [オプション]...　　　　　各種IaaSプラットフォームの新規追加を行います。\n"
            "　　pcc add instancetype [オプション]...　　　プラットフォームのインスタンスタイプ新規追加を行います。\n"
            "　　pcc add storagetype [オプション]...　　　 プラットフォームのストレージタイプ新規追加を行います。\n"
            "　　pcc add image [オプション]...　　　　　　 OSイメージの新規追加を行います。\n")

def updateHelp():
    return("概要\n"
            "　　PCCの各種データを更新するためのコマンドです。\n"
            "使い方\n"
            "　　pcc update [ターゲット] [オプション]...\n"
            "コマンド一覧\n"
            "　　pcc update platform [オプション]...　　　 登録されたプラットフォーム情報の更新を行います。\n"
            "　　pcc update instancetype [オプション]...　 プラットフォームのインスタンスタイプ更新を行います。\n"
            "　　pcc update storagetype [オプション]...　　プラットフォームのストレージタイプ更新を行います。\n"
            "　　pcc update image [オプション]...　　　　　登録されたOSイメージの情報の更新を行います。\n")

def deleteHelp():
    return("概要\n"
            "　　PCCの各種データを削除するためのコマンドです。\n"
            "使い方\n"
            "　　pcc del [ターゲット] [オプション]...\n"
            "コマンド一覧\n"
            "　　pcc del platform [オプション]...　　　　　登録されたプラットフォーム情報の削除を行います。\n"
            "　　pcc del instancetype [オプション]...　　　プラットフォームのインスタンスタイプ削除を行います。\n"
            "　　pcc del storagetype [オプション]...　　　 プラットフォームのストレージタイプ削除を行います。\n"
            "　　pcc del image [オプション]...　　　　　　 登録されたOSイメージの情報の削除を行います。\n")

def enableHelp():
    return("概要\n"
            "　　PCCの各種データを有効化するためのコマンドです。\n"
            "使い方\n"
            "　　pcc enable [ターゲット] [オプション]...\n"
            "コマンド一覧\n"
            "　　pcc enable platform [オプション]...　　　 指定されたプラットフォームの有効化を行います。\n"
            "　　pcc enable image [オプション]...　　　　　指定されたOSイメージの有効化を行います。\n")

def disableHelp():
    return("概要\n"
            "　　PCCの各種データを無効化するためのコマンドです。\n"
            "使い方\n"
            "　　pcc disable [ターゲット] [オプション]...\n"
            "コマンド一覧\n"
            "　　pcc disable platform [オプション]...　　　指定されたプラットフォームの無効化を行います。\n"
            "　　pcc disable image [オプション]...　　　　 指定されたOSイメージの無効化を行います。\n")

def listHelp():
    return("概要\n"
            "　　PCCの各種データを一覧表示するためのコマンドです。\n"
            "使い方\n"
            "　　pcc list [ターゲット] [オプション]...\n"
            "コマンド一覧\n"
            "　　pcc list iaas [オプション]...　　　　　　 PCCがサポートしているIaaSの一覧表示を行います。\n"
            "　　pcc list platform [オプション]...　　　　 プラットフォームの一覧表示を行います。\n"
            "　　pcc list instancetype [オプション]...　　 インスタンスタイプの一覧表示を行います。\n"
            "　　pcc del storagetype [オプション]...　　　 プラットフォームのストレージタイプ削除を行います。\n"
            "　　pcc list image [オプション]...　　　　　　OSイメージの一覧表示を行います。\n")

def showHelp():
    return("概要\n"
            "　　PCCの各種データの詳細を表示するためのコマンドです。\n"
            "使い方\n"
            "　　pcc show [ターゲット] [オプション]...\n"
            "コマンド一覧\n"
            "　　pcc show iaas [オプション]...　　　　　　 PCCがサポートしているIaaSの詳細情報表示を行います。\n"
            "　　pcc show platform [オプション]...　　　　 指定されたプラットフォームの詳細情報表示を行います。\n"
            "　　pcc show image [オプション]...　　　　　　指定されたOSイメージの詳細情報表示を行います。\n")

def addPlatformHelp():
    return ("概要\n"
            "　　各種IaaSプラットフォームの新規追加を行います。\n"
            "使い方\n"
            "　　pcc add platform [オプション]...\n"
            "指定可能な引数\n"
            "　　--iaasName [IaaS名称]　　　　　　　　　　　　　 必須　IaaSの種類を指定します。\n"
            "　　--platformName [プラットフォーム名称]　　　　　 必須　プラットフォーム名称を指定します。\n"
            "　　--platformNameDisp [表示用プラットフォーム名称] 必須　表示用プラットフォーム名称を指定します。\n"
            "　　--platformSimpleDisp [プラットフォーム短縮名称] 必須　表示用プラットフォーム短縮名称を指定します。\n"
            "　　--internal [0(パブリック)/1(プライベート)]　　　任意　プライベートクラウドフラグを指定します。省略時:0\n"
            "　　--proxy [0(未使用)/1(使用)]　　　　　　　　　　 任意　proxy使用フラグを指定します。省略時:0\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n"
            "\n"
            "<iaasNameがawsの場合に指定可能>\n"
            "　　--endpoint [API-URL]　　　　　　　　　　　　　　必須　APIのホストアドレスを指定します。\n"
            "　　--secure [0(未使用)/1(使用)]　　　　　　　　　　任意　APIへのアクセス方法のHTTPS使用フラグを指定します。省略時:0\n"
            "　　--euca [0(未使用)/1(使用)]　　　　　　　　　　　任意　Eucalyptusのフラグを指定します。省略時:0\n"
            "　　--vpc [0(未使用)/1(使用)]　　　　　　　　　　　 任意　VPCの使用フラグを指定します。省略時:0\n"
            "　　--region [リージョン名称]　　　　　　　　　　　 任意　リージョンを指定します。\n"
            "　　--availabilityzone [ゾーン名称]　　　　　　　　 任意　アベイラビリティゾーンを指定します。\n"
            "　　--vpcId [VPC ID]　　　　　　　　　　　　　　　　任意　VPC-IDを指定します。\n"
            "\n"
            "<iaasNameがvmwareの場合に指定可能>\n"
            "　　--endpoint [API-URL]　　　　　　　　　　　　　　必須　APIのURLを指定します。\n"
            "　　--username [API-ユーザ名]　　　　　　　　　　　 必須　APIのユーザ名を指定します。\n"
            "　　--password [API-パスワード]　　　　　　　　　　 必須　APIのパスワードを指定します。\n"
            "　　--datacenter [データセンター名称]　　　　　　　 必須　操作対象のデータセンター名を指定します。\n"
            "　　--publicnetwork [デフォルトグループ名称]　　　　必須　パブリック用のデフォルトグループ名を指定します。\n"
            "　　--privatenetwork [デフォルトグループ名称]　　　 必須　プライベート用のデフォルトグループ名を指定します。\n"
            "　　--computeresource [ホスト／クラスタ名称]　　　　必須　VMwareで使用するサーバのホスト、クラスタ名を指定します。\n"
            "　　--instanceTypeName [インスタンスタイプ名称]　　 必須　インスタンスタイプ名称を指定します。\n"
            "　　--cpu [CPU数]　　　　　　　　　　　　　　　　　 必須　インスタンスで使用するCPU数を指定します。\n"
            "　　--memory [メモリサイズ]　　　　　　　　　　　　 必須　インスタンスで使用するメモリ(MByte)を指定します。\n"
            "\n"
            "<iaasNameがcloudstackの場合に指定可能>\n"
            "　　--endpoint [API-URL]　　　　　　　　　　　　　　必須　APIのURLを指定します。\n"
            "　　--secure [0(未使用)/1(使用)]　　　　　　　　　　任意　APIへのアクセス方法のHTTPS使用フラグを指定します。省略時:0\n"
            "　　--zoneId [ゾーンID]　　　　　　　　　　　　　　 必須　ゾーンIDを指定します。\n"
            "　　--networkId [ネットワークID]　　　　　　　　　　必須　ネットワークIDを指定します。カンマ区切りで複数指定可能です。\n"
            "　　--timeout [タイムアウト秒数]　　　　　　　　　　任意　タイムアウトするまでの時間(秒)を指定します。\n"
            "　　--deviceType [デバイスタイプ]　　　　　　　　　 必須　デバイスタイプを指定します。\n"
            "　　--hostId [ホストID]　　　　　　　　　　　　　　 任意　ホストIDを指定します。\n"
            "\n"
            "<iaasNameがvCloudの場合に指定可能>\n"
            "　　--endpoint [API-URL]　　　　　　　　　　　　　　必須　APIのURLを指定します。\n"
            "　　--orgName [組織名称]　　　　　　　　　　　　　　必須　組織名称を指定します。\n"
            "　　--vdcName [VDC名称]　　　　　　　　　　　　　　 必須　VDC名称を指定します。\n"
            "　　--secure [0(未使用)/1(使用)]　　　　　　　　　　任意　APIへのアクセス方法のHTTPS使用フラグを指定します。省略時:0\n"
            "　　--timeout [タイムアウト秒数]　　　　　　　　　　任意　タイムアウトするまでの時間(秒)を指定します。\n"
            "　　--defNetwork [ネットワーク名称]　　　　　　　　 任意　初期値として使用するネットワーク名称を指定します。\n"
            "　　--instanceTypeName [インスタンスタイプ名称]　　 必須　インスタンスタイプ名称を指定します。\n"
            "　　--cpu [CPU数]　　　　　　　　　　　　　　　　　 必須　インスタンスで使用するCPU数を指定します。\n"
            "　　--memory [メモリサイズ]　　　　　　　　　　　　 必須　インスタンスで使用するメモリ(MByte)を指定します。\n"
            "　　--storageTypeName [ストレージタイプ名称]　　　　必須　インスタンスタイプ名称を指定します。\n"
            "\n"
            "<iaasNameがopenstackの場合に指定可能>\n"
            "　　--endpoint [API-URL]　　　　　　　　　　　　　　必須　APIのURLを指定します。\n"
            "　　--networkId [ネットワークID]　　　　　　　　　　必須　ネットワークIDを指定します。\n"
            "　　--tenantId [テナントID]　　　　　　　　　　　　 必須　テナントIDを指定します。\n"
            "　　--tenantNm [テナント名称]　　　　　　　　　　　 任意　テナント名称を指定します。\n"
            "　　--availabilityZone [ゾーン名称]　　　　　　　　 任意　アベイラビリティゾーンを指定します。\n"
            "\n"
            "<iaasNameがazureの場合に指定可能>\n"
            "　　--region [リージョン名称]　　　　　　　　 　　　必須　リージョンを指定します。\n"
            "　　--affinityGroupName [アフィニティグループ名称]　必須　アフィニティグループ名称を指定します。\n"
            "　　--cloudServiceName [クラウドサービス名称]　　　 必須　クラウドサービス名称を指定します。\n"
            "　　--storageAccountName [ストレージアカウント名称] 必須　ストレージアカウント名称を指定します。\n"
            "　　--networkName [ネットワーク名称]　　　　　　　　必須　ネットワーク名称を指定します。\n"
            "　　--availabilitySets [可用性セット]　　　　　　　 任意　可用性セットを指定します。カンマ区切りで複数指定可能です。\n")

def updatePlatformHelp():
    return("概要\n"
            "　　登録されたプラットフォーム情報の更新を行います。\n"
            "使い方\n"
            "　　pcc update platform [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformNo [プラットフォームNo]　　　　　　　 必須　更新対象を特定するプラットフォームNoを指定します。\n"
            "　　--platformName [プラットフォーム名称]　　　　　 任意　プラットフォーム名称を指定します。\n"
            "　　--platformNameDisp [表示用プラットフォーム名称] 任意　表示用プラットフォーム名称を指定します。\n"
            "　　--platformSimpleDisp [プラットフォーム短縮名称] 任意　表示用プラットフォーム短縮名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n"
            "\n"
            "<iaasがawsの場合に指定可能>\n"
            "　　--endpoint [API-URL]　　　　　　　　　　　　　　任意　APIのホストアドレスを指定します。\n"
            "\n"
            "<iaasがvmwareの場合に指定可能>\n"
            "　　--endpoint [API-URL]　　　　　　　　　　　　　　任意　APIのURLを指定します。\n"
            "　　--username [API-ユーザ名]　　　　　　　　　　　 任意　APIのユーザ名を指定します。\n"
            "　　--password [API-パスワード]　　　　　　　　　　 任意　APIのパスワードを指定します。\n"
            "　　--publicnetwork [デフォルトグループ名]　　　　　任意　パブリック用のデフォルトグループ名を指定します。\n"
            "　　--privatenetwork [デフォルトグループ名]　　　　 任意　プライベート用のデフォルトグループ名を指定します。\n"
            "\n"
            "<iaasがcloudstackの場合に指定可能>\n"
            "　　--endpoint [API-URL]　　　　　　　　　　　　　　任意　APIのURLを指定します。\n"
            "　　--timeout [タイムアウト秒数]　　　　　　　　　　任意　タイムアウトまでの時間を指定します。\n"
            "\n"
            "<iaasがvcloudの場合に指定可能>\n"
            "　　--endpoint [API-URL]　　　　　　　　　　　　　　任意　APIのURLを指定します。\n"
            "　　--timeout [タイムアウト秒数]　　　　　　　　　　任意　タイムアウトまでの時間を指定します。\n"
            "　　--defNetwork [デフォルトネットワーク]　　　　　 任意　使用するネットワーク名の初期値を指定します。\n"
            "\n"
            "<iaasがopenstackの場合に指定可能>\n"
            "　　--endpoint [API-URL]　　　　　　　　　　　　　　任意　APIのURLを指定します。\n")

def deletePlatformHelp():
    return("概要\n"
            "　　登録されたプラットフォーム情報の削除を行います。\n"
            "使い方\n"
            "　　pcc del platform [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 必須　削除対象のプラットフォーム名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def enablePlatformHelp():
    return("概要\n"
            "　　プラットフォームの有効化を行います。\n"
            "使い方\n"
            "　　pcc enable platform [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 必須　有効化対象のプラットフォーム名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def disablePlatformHelp():
    return("概要\n"
            "　　プラットフォームの無効化を行います。\n"
            "使い方\n"
            "　　pcc disable platform [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 必須　無効化対象のプラットフォーム名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def listPlatformHelp():
    return("概要\n"
            "　　プラットフォームの一覧表示を行います。\n"
            "使い方\n"
            "　　pcc list platform [オプション]...\n"
            "指定可能な引数\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def showPlatformHelp():
    return("概要\n"
            "　　指定されたプラットフォームの詳細情報を表示します。\n"
            "使い方\n"
            "　　pcc show platform [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 必須　参照対象プラットフォーム名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def addInstanceTypeHelp():
    return("概要\n"
            "　　プラットフォームのインスタンスタイプ新規登録を行います。\n"
            "使い方\n"
            "　　pcc add instancetype [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 必須　追加対象プラットフォーム名称を指定します。\n"
            "　　--instanceTypeName [インスタンスタイプ名称]　　 必須　インスタンスタイプの名称を指定します。\n"
            "　　--cpu [CPU数]　　　　　　　　　　　　　　　　　 必須　インスタンスで使用するCPU数を指定します。\n"
            "　　--memory [メモリサイズ]　　　　　　　　　　　　 必須　インスタンスで使用するメモリ(MByte)を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def updateInstanceTypeHelp():
    return("概要\n"
            "　　プラットフォームのインスタンスタイプ更新を行います。\n"
            "使い方\n"
            "　　pcc update instancetype [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 必須　更新対象プラットフォームの名称を指定します。\n"
            "　　--instanceTypeNo [インスタンスタイプNo]　　　　 必須　更新対象インスタンスタイプNoを指定します。\n"
            "　　--instanceTypeName [インスタンスタイプ名称]　　 任意　インスタンスタイプの名称を指定します。\n"
            "　　--cpu [CPU数]　　　　　　　　　　　　　　　　　 任意　インスタンスタイプで使用するCPU数を指定します。\n"
            "　　--memory [メモリサイズ]　　　　　　　　　　　　 任意　インスタンスタイプで使用するメモリ(MByte)を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def deleteInstanceTypeHelp():
    return("概要\n"
            "　　プラットフォームのインスタンスタイプ削除を行います。\n"
            "使い方\n"
            "　　pcc del instancetype [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 必須　プラットフォーム名を指定します。\n"
            "　　--instanceTypeNo [インスタンスタイプNo]　　　　 必須　削除対象インスタンスタイプNoを指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def listInstanceTypeHelp():
    return("概要\n"
            "　　インスタンスタイプの一覧表示を行います。\n"
            "使い方\n"
            "　　pcc list instancetype [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 任意　インスタンスタイプを表示するプラットフォーム名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def addStorageTypeHelp():
    return("概要\n"
            "　　プラットフォームのストレージタイプ新規登録を行います。\n"
            "使い方\n"
            "　　pcc add storagetype [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 必須　追加対象プラットフォーム名称を指定します。\n"
            "　　--storageTypeName [ストレージタイプ名称]　　　　必須　ストレージタイプの名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def updateStorageTypeHelp():
    return("概要\n"
            "　　プラットフォームのストレージタイプ更新を行います。\n"
            "使い方\n"
            "　　pcc update storagetype [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 必須　更新対象のプラットフォーム名称を指定します。\n"
            "　　--storageTypeNo [ストレージタイプNo]　　　　　　必須　更新対象のストレージタイプNoを指定します。\n"
            "　　--storageTypeName [ストレージタイプ名称]　　　　任意　ストレージタイプの名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def deleteStorageTypeHelp():
    return("概要\n"
            "　　プラットフォームのストレージタイプ削除を行います。\n"
            "使い方\n"
            "　　pcc del storagetype [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 必須　削除対象のプラットフォーム名称を指定します。\n"
            "　　--storageTypeNo [ストレージタイプNo]　　　　　　必須　削除対象のストレージタイプNoを指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def listStorageTypeHelp():
    return("概要\n"
            "　　ストレージタイプの一覧表示を行います。\n"
            "使い方\n"
            "　　pcc list storagetype [オプション]...\n"
            "指定可能な引数\n"
            "　　--platformName [プラットフォーム名称]　　　　　 任意　ストレージタイプを表示するプラットフォーム名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def listIaasHelp():
    return("概要\n"
            "　　サポートしているIaaSの一覧表示を行います。\n"
            "使い方\n"
            "　　pcc list iaas [オプション]...\n"
            "指定可能な引数\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def showIaasHelp():
    return("概要\n"
            "　　サポートしているIaaSの情報表示を行います。\n"
            "使い方\n"
            "　　pcc show iaas [オプション]...\n"
            "指定可能な引数\n"
            "　　--iaasName [IaaS名称]　　　　　　　　　　　　　 必須　参照対象IaaS名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def addImageHelp():
    return("概要\n"
            "　　OSイメージの新規追加を行います。\n"
            "使い方\n"
            "　　pcc add image [オプション]...\n"
            "指定可能な引数\n"
            "　　--moduleName [モジュール名称]　　　　　　　　　 必須　登録するモジュールの名称を指定します。\n"
            "　　--platformList [プラットフォームリスト]　　　　 任意　イメージを登録するプラットフォームを指定します。カンマ区切りで複数指定可能です。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def updateImageHelp():
    return("概要\n"
            "　　OSイメージの更新を行います。\n"
            "使い方\n"
            "　　pcc update image [オプション]...\n"
            "指定可能な引数\n"
            "　　--imageNo [イメージNo]　　　　　　　　　　　　　必須　更新対象のイメージNoを指定します。\n"
            "　　--imageName [イメージ名称]　　　　　　　　　　　任意　登録するイメージの名称を指定します。\n"
            "　　--imageNameDisp [画面表示用イメージ名称]　　　　任意　画面表示用のイメージ名称を指定します。\n"
            "　　--osName [OS名称]　　　　　　　　　　　　　　　 任意　OS名称を指定します。\n"
            "　　--osNameDisp [画面表示用OS名称]　　　　　　　　 任意　画面表示用のOS名称を指定します。\n"
            "　　--serviceList [使用可能サービスリスト]　　　　　任意　使用可能なサービス名を指定します。カンマ区切りで複数指定可能です。\n"
            "　　--instanceTypeList [インスタンスタイプリスト]　 任意　使用可能なインスタンスタイプを指定します。カンマ区切りで複数指定可能です。\n"
            "　　--zabbixTemplate [ZABBIXテンプレート名称]　　　 任意　使用するZabbixのテンプレート名称を指定します。\n"
            "　　--icon [アイコン画像パス]　　　　　　　　　　　 任意　アイコンファイルのパスを指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n"
            "\n"
            "<プラットフォームがawsの場合に指定可能>\n"
            "　　--kernelId [カーネルID]　　　　　　　　　　　　 任意　カーネルIDを指定します。\n"
            "　　--ramdiskId [RAMDISK ID]　　　　　　　　　　　　任意　RAMディスクIDを指定します。\n")

def deleteImageHelp():
    return("概要\n"
            "　　OSイメージの削除を行います。\n"
            "使い方\n"
            "　　pcc del image [オプション]...\n"
            "指定可能な引数\n"
            "　　--moduleName [モジュール名称]　　　　　　　　　 必須　削除対象のモジュール名称を指定します。\n"
            "　　--platformList [プラットフォームリスト]　　　　 任意　指定すると指定したプラットフォームからのみ削除します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def enableImageHelp():
    return("概要\n"
            "　　指定されたOSイメージの有効化を行います。\n"
            "使い方\n"
            "　　pcc enable image [オプション]...\n"
            "指定可能な引数\n"
            "　　--imageNo [イメージNo]　　　　　　　　　　　　　必須　有効化対象のイメージNoを指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def disableImageHelp():
    return("概要\n"
            "　　指定されたOSイメージの無効化を行います。\n"
            "使い方\n"
            "　　pcc disable image [オプション]...\n"
            "指定可能な引数\n"
            "　　--imageNo [イメージNo]　　　　　　　　　　　　　必須　無効化対象のイメージNoを指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def listImageHelp():
    return("概要\n"
            "　　OSイメージの一覧表示を行います。\n"
            "使い方\n"
            "　　pcc list image [オプション]...\n"
            "指定可能な引数\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def showImageHelp():
    return("概要\n"
            "　　OSイメージの詳細表示を行います。\n"
            "使い方\n"
            "　　pcc show image [オプション]...\n"
            "指定可能な引数\n"
            "　　--imageNo [イメージNo]　　　　　　　　　　　　　必須　参照対象のイメージNoを指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def addServiceHelp():
    return("概要\n"
            "　　サービスの新規追加を行います。\n"
            "使い方\n"
            "　　pcc add service [オプション]...\n"
            "指定可能な引数\n"
            "　　--moduleName [モジュール名称]　　　　　　　　　 必須　追加するモジュール名称を指定します。\n"
            "　　--imageNoList [イメージNoリスト]　　　　　　　　任意　サービスを登録するイメージNoのリストを指定します。カンマ区切りで複数指定可能です。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def updateServiceHelp():
    return("概要\n"
            "　　\n"
            "使い方\n"
            "　　pcc update service [オプション]...\n"
            "指定可能な引数\n"
            "　　--serviceName [サービス名称]　　　　　　　　　　必須　最新化するサービスの名称を指定します。\n"
            "　　--serviceNameDisp [画面表示用サービス名称]　　　任意　画面表示用のサービス名称を指定します。\n"
            "　　--layer [レイヤー名称]　　　　　　　　　　　　　任意　レイヤー名称を指定します。\n"
            "　　--layerNameDisp [画面表示用レイヤー名称]　　　　任意　画面表示用のレイヤー名称を指定します。\n"
            "　　--runOrder [起動優先順]　　　　　　　　　　　　 任意　起動優先順を指定します。\n"
            "　　--zabbixTemplate [zabbixテンプレート名称]　　　 任意　zabbixのテンプレート名称を指定します。\n"
            "　　--addressUrl [アドレス]　　　　　　　　　　　　 任意　URLを指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def deleteServiceHelp():
    return("概要\n"
            "　　サービスの削除を行います。\n"
            "使い方\n"
            "　　pcc del service [オプション]...\n"
            "指定可能な引数\n"
            "　　--moduleName [モジュール名称]　　　　　　　　　 必須　削除するモジュール名称を指定します。\n"
            "　　--imageNoList [イメージNoリスト]　　　　　　　　任意　サービスを削除するイメージNoのリストを指定します。カンマ区切りで複数指定可能です。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def enableServiceHelp():
    return ("概要\n"
            "　　指定したサービスを有効化します。\n"
            "使い方\n"
            "　　pcc enable service [オプション]...\n"
            "指定可能な引数\n"
            "　　--serviceName [サービス名称]　　　　　　　　　　必須　有効化対象のサービス名称を指定します。"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。"
            )

def disableServiceHelp():
    return ("概要\n"
            "　　指定したサービスを無効化します。\n"
            "使い方\n"
            "　　pcc disable service [オプション]...\n"
            "指定可能な引数\n"
            "　　--serviceName [サービス名称]　　　　　　　　　　必須　無効化対象のサービス名称を指定します。"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。"
            )

def listServiceHelp():
    return("概要\n"
            "　　リポジトリサーバに登録されているサービスモジュールのリストを表示します。\n"
            "使い方\n"
            "　　pcc list service [オプション]...\n"
            "指定可能な引数\n"
            "　　--installed　　　　　　　　　　　　　　　　　　 任意　インストール済みのサービスモジュールのみを表示します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def showServiceHelp():
    return("概要\n"
            "　　リポジトリサーバに登録されているサービスモジュールの詳細情報を表示します。\n"
            "使い方\n"
            "　　pcc show service [オプション]...\n"
            "指定可能な引数\n"
            "　　--serviceModule [サービスパッケージ名称]　　　　必須　詳細表示するサービスパッケージ名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def validateServiceHelp():
    return("概要\n"
            "　　イメージ上で利用可能なサービス情報の追加を行います。\n"
            "使い方\n"
            "　　pcc validate service [オプション]...\n"
            "指定可能な引数\n"
            "　　--imageNo [イメージNo]　　　　　　　　　　　　　必須　サービス情報追加対象のイメージNoを指定します。\n"
            "　　--serviceList [サービスリスト]　　　　　　　　　必須　追加するサービス名称を指定します。カンマ区切りで複数指定可能です。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def revokeServiceHelp():
    return("概要\n"
            "　　イメージ上で利用可能なサービス情報の削除を行います。\n"
            "使い方\n"
            "　　pcc revoke service [オプション]...\n"
            "指定可能な引数\n"
            "　　--imageNo [イメージNo]　　　　　　　　　　　　　必須　サービス情報削除対象のイメージNoを指定します。\n"
            "　　--serviceList [サービスリスト]　　　　　　　　　必須　削除するサービス名称を指定します。カンマ区切りで複数指定可能です。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def pccrepoHelp():
    return("概要\n"
            "　　リポジトリサーバへのアクセスを行うコマンドです。\n"
            "使い方\n"
            "　　pccrepo [コマンド] [オプション]...\n"
            "コマンド一覧\n"
            "　　pccrepo install [オプション]...　　　　　 リポジトリサーバに登録されているモジュールのインストールを行います。\n"
            "　　pccrepo remove [オプション]...　　　　　　指定したモジュールをアンインストールします。\n"
            "　　pccrepo update [オプション]...　　　　　　指定したモジュールをアップデートします。\n"
            "　　pccrepo list [オプション]...　　　　　　　リポジトリサーバに登録されているモジュールの一覧表示を行います。\n"
            "　　pccrepo show [オプション]...　　　　　　　指定したモジュールの詳細情報を表示します。\n"
            "　　pccrepo clean [オプション]...　　　　　　 PCCリポジトリのキャッシュをクリアします。\n")

def installModuleHelp():
    return("概要\n"
            "　　リポジトリサーバに登録されているモジュールのインストールを行います。\n"
            "使い方\n"
            "　　pccrepo install [オプション]...\n"
            "指定可能な引数\n"
            "　　--moduleName [モジュール名称]　　　　　　　　　 必須　インストールするモジュール名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def removeModuleHelp():
    return("概要\n"
            "　　指定したモジュールをアンインストールします。\n"
            "使い方\n"
            "　　pccrepo remove [オプション]...\n"
            "指定可能な引数\n"
            "　　--moduleName [モジュール名称]　　　　　　　　　 必須　アンインストールするモジュール名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def updateModuleHelp():
    return("概要\n"
            "　　指定したモジュールをアップデートします。\n"
            "使い方\n"
            "　　pccrepo update [オプション]...\n"
            "指定可能な引数\n"
            "　　--moduleName [モジュール名称]　　　　　　　　　 必須　アップデートするモジュール名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def listModuleHelp():
    return("概要\n"
            "　　リポジトリサーバに登録されているモジュールの一覧表示を行います。\n"
            "使い方\n"
            "　　pccrepo list [オプション]...\n"
            "指定可能な引数\n"
            "　　--installed　　　　　　　　　　　　　　　　　　 任意　指定するとインストール済のモジュールのみを表示します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def showModuleHelp():
    return("概要\n"
            "　　指定したモジュールの詳細情報を表示します。\n"
            "使い方\n"
            "　　pccrepo show [オプション]...\n"
            "指定可能な引数\n"
            "　　--moduleName [モジュール名称]　　　　　　　　　 必須　情報表示するモジュール名称を指定します。\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

def cleanCacheHelp():
    return("概要\n"
            "　　PCCリポジトリのキャッシュをクリアします。\n"
            "使い方\n"
            "　　pccrepo clean [オプション]...\n"
            "指定可能な引数\n"
            "　　-h,--help　　　　　　　　　　　　　　　　　　　 任意　このヘルプを表示します。\n")

if __name__ == '__main__':
    command = sys.argv[1]
    if "pccHelp" == command:
        print(pccHelp())
    elif "addHelp" == command:
        print(addHelp())
    elif "updateHelp" == command:
        print(updateHelp())
    elif "deleteHelp" == command:
        print(deleteHelp())
    elif "enableHelp" == command:
        print(enableHelp())
    elif "disableHelp" == command:
        print(disableHelp())
    elif "listHelp" == command:
        print(listHelp())
    elif "showHelp" == command:
        print(showHelp())
    elif "addPlatform" == command:
        print(addPlatformHelp())
    elif "updatePlatform" == command:
        print(updatePlatformHelp())
    elif "deletePlatform" == command:
        print(deletePlatformHelp())
    elif "enablePlatform" == command:
        print(enablePlatformHelp())
    elif "disablePlatform" == command:
        print(disablePlatformHelp())
    elif "listPlatform" == command:
        print(listPlatformHelp())
    elif "showPlatform" == command:
        print(showPlatformHelp())
    elif "addInstanceType" == command:
        print(addInstanceTypeHelp())
    elif "updateInstanceType" == command:
        print(updateInstanceTypeHelp())
    elif "deleteInstanceType" == command:
        print(deleteInstanceTypeHelp())
    elif "listInstanceType" == command:
        print(listInstanceTypeHelp())
    elif "addStorageType" == command:
        print(addStorageTypeHelp())
    elif "updateStorageType" == command:
        print(updateStorageTypeHelp())
    elif "deleteStorageType" == command:
        print(deleteStorageTypeHelp())
    elif "listStorageType" == command:
        print(listStorageTypeHelp())
    elif "listIaas" == command:
        print(listIaasHelp())
    elif "showIaas" == command:
        print(showIaasHelp())
    elif "addImage" == command:
        print(addImageHelp())
    elif "updateImage" == command:
        print(updateImageHelp())
    elif "deleteImage" == command:
        print(deleteImageHelp())
    elif "enableImage" == command:
        print(enableImageHelp())
    elif "disableImage" == command:
        print(disableImageHelp())
    elif "listImage" == command:
        print(listImageHelp())
    elif "showImage" == command:
        print(showImageHelp())
    elif "addService" == command:
        print(addServiceHelp())
    elif "updateService" == command:
        print(updateServiceHelp())
    elif "deleteService" == command:
        print(deleteServiceHelp())
    elif "enableService" == command:
        print(enableServiceHelp())
    elif "disableService" == command:
        print(disableServiceHelp())
    elif "listService" == command:
        print(listServiceHelp())
    elif "showService" == command:
        print(showServiceHelp())
    elif "validateService" == command:
        print(validateServiceHelp())
    elif "revokeService" == command:
        print(revokeServiceHelp())
    #pccrepoコマンドのヘルプ
    elif "pccrepoHelp" == command:
        print(pccrepoHelp())
    elif "installModule" == command:
        print(installModuleHelp())
    elif "removeModule" == command:
        print(removeModuleHelp())
    elif "updateModule" == command:
        print(updateModuleHelp())
    elif "listModule" == command:
        print(listModuleHelp())
    elif "showModule" == command:
        print(showModuleHelp())
    elif "cleanCache" == command:
        print(cleanCacheHelp())