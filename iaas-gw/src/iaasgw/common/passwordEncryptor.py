 # coding: UTF-8
 #
 # Copyright 2014 by SCSK Corporation.
 # 
 # This file is part of PrimeCloud Controller(TM).
 # 
 # PrimeCloud Controller(TM) is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 2 of the License, or
 # (at your option) any later version.
 # 
 # PrimeCloud Controller(TM) is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 # GNU General Public License for more details.
 # 
 # You should have received a copy of the GNU General Public License
 # along with PrimeCloud Controller(TM). If not, see <http://www.gnu.org/licenses/>.
 # 
from Crypto.Cipher import AES
import base64

BS = 16
pad = lambda s: s + (BS - len(s) % BS) * chr(BS - len(s) % BS)
unpad = lambda s : s[0:-ord(s[-1])]

class PasswordEncryptor:

# 今回使用しないことやテスト未実施の為、コメント化
#    # パスワード暗号化
#    def encrypt(self, password, salt):
#        password = pad(password)
#        iv = ''.join(chr(x) for x in [0x00, 0x01, 0x02, 0x03, \
#                                      0x04, 0x05, 0x06, 0x07, \
#                                      0x08, 0x09, 0x0a, 0x0b, \
#                                      0x0c, 0x0d, 0x0e, 0x0f])
#        cipher = AES.new(salt, AES.MODE_CBC, iv)
#        return base64.b64encode(cipher.encrypt(password))

    # パスワード復号化
    def decrypt(self, password, salt):
        password = base64.b64decode(password)
        iv = ''.join(chr(x) for x in [0x00, 0x01, 0x02, 0x03, \
                                      0x04, 0x05, 0x06, 0x07, \
                                      0x08, 0x09, 0x0a, 0x0b, \
                                      0x0c, 0x0d, 0x0e, 0x0f])
        cipher = AES.new(salt, AES.MODE_CBC, iv)
        return unpad(cipher.decrypt(password))
