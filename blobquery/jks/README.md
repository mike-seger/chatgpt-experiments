```
keytool -genkeypair -alias mykey -keyalg RSA -keysize 2048 -validity 365 -keystore keystore.jks
```

```
keytool -exportcert -alias mykey -keystore keystore.jks -rfc -file mykey.crt
```

```
keytool -importcert -alias mykey -file mykey.crt -keystore truststore.jks
```