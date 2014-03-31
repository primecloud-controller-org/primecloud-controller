Option Explicit

Dim count
count = 1
Dim objShell
Set objShell = WScript.CreateObject("WScript.Shell")
Dim logFS
Dim log
Set logFS = WScript.CreateObject("Scripting.FileSystemObject")
Dim logFileName
Dim logFile
logFileName = "C:\Program Files\PCC\script\log\network-init.txt"
If logFS.FileExists(logFileName) = False Then
	Set log = logFS.CreateTextFile(logFileName)
Else
	Set logFile = logFS.getFile(logFileName)
	Set log = logFile.OpenAsTextStream(8)
End If
	log.WriteLine Now() & " Network initialize start"

	'Countの数だけGuestInfoを取る
	Do 
		Dim command
		command = """C:\Program Files\VMware\VMware Tools\vmtoolsd.exe"" --cmd ""info-get guestinfo.network" & count & """"
		Dim objExec
		Set objExec = objShell.Exec(command)
	
		Do While objExec.Status = 0
			WScript.Sleep 100
	  	Loop
	    Dim strLine
		strLine = objExec.StdOut.ReadLine 
			'GuestInfoが取れなかったら終わり
			If Len(strLine) = 0 Then
    	    	Exit Do         
			End If
		log.WriteLine Now() & " guestInfo.network" & count & " " & strLine

		Dim GuestInfos 
		GuestInfos = Split(strLine,";")
		
		Dim NetworkConfig_
		Set NetworkConfig_ = new NetworkConfig
		Dim GuestInfo
		For Each GuestInfo in GuestInfos
			Dim Key
			Dim Value
			Key = Split(GuestInfo,"=")(0)
			Value = Split(GuestInfo,"=")(1)
			Select Case Key
				Case "BootProto"
					NetworkConfig_.BootProto = Value
				Case "Mac"
					NetworkConfig_.MAC = Value
				Case "IP"
					NetworkConfig_.IP = Value
				Case "Netmask"
					NetworkConfig_.Netmask = Value
				Case "Gateway"
					NetworkConfig_.Gateway = Value
			End Select  
		Next
		Dim NetworkAdapters
		Dim Locator
		Dim Service
		Set Locator = WScript.CreateObject("WbemScripting.SWbemLocator")
		Set Service = Locator.ConnectServer
		Set NetworkAdapters = Service.ExecQuery _
		("select * from Win32_NetworkAdapterConfiguration Where IPEnabled = true")
		Dim Adapter
		For Each Adapter In NetworkAdapters
			If Adapter.MACAddress = NetworkConfig_.MAC  Then
				Dim Ret
				Ret = -1
				If NetworkConfig_.BootProto = "static" Then
					'Staticを設定する
					Dim IPAdd(0)
					Dim Netmask(0)
					IPAdd(0) = NetworkConfig_.IP
					Netmask(0) = NetworkConfig_.Netmask
					Dim Retry
					Retry = 1
					Do While Retry < 4
						'5秒Sleepを入れて3回リトライする
						WScript.Sleep(5000)
						Ret = Adapter.EnableStatic(IPAdd, Netmask)
						If Ret = 0 Then
							log.WriteLine Now() & " Static IP setting was successful. IP:" & IPAdd(0) & " Netmask:" & Netmask(0)
							Exit Do
						Else
							log.WriteLine Now() & " Static IP setting was faild. ReturnCode:" & Ret & " Retry:" & Retry &" IP:" & IPAdd(0) & " Netmask:" & Netmask(0)
							Retry = Retry + 1
						End If
					Loop
					Dim GWAdd(0)
					GWAdd(0) = NetworkConfig_.Gateway
					Ret = Adapter.SetGateways(GWAdd)
					If Ret = 0 Then
						log.WriteLine Now() & " Gateway setting was successful. Gateway:" & GWAdd(0)
					Else
						log.WriteLine Now() & " Gateway setting was faild.  ReturnCode:" & Ret & " Gateway:" & GWAdd(0)
					End If
				End If
				If NetworkConfig_.BootProto = "dhcp" Then
					'DHCPを設定する
					Ret = Adapter.EnableDHCP()
					If Ret = 0 Then
						log.WriteLine Now() & " DHCP setting was successful."
					Else
						log.WriteLine Now() & " Gateway setting was faild.  ReturnCode:" & Ret
					End If
				End If
			End If
			
		Next
		Set NetworkAdapters = Nothing
		Set Locator = Nothing
		Set Service = Nothing	

		Set NetworkConfig_ = Nothing
	count = count + 1
	Loop
	
log.WriteLine Now() & " Network initialize end"
log.Close
Set log = Nothing
Set logFile = Nothing
Set logFileName = Nothing
Set logFS = Nothing
Set count = Nothing	


Class NetworkConfig

  Public IP
  Public MAC
  Public BootProto
  Public Netmask
  Public Gateway
  
End Class
  