# mysql2excel

    通过db.properties导出excel小工具
    
## 功能

+ 通过数据源导出xls xlsx
+ 支持筛选指定table
    

## 使用准备

### 数据源:  db.properties (参考)
    datasource.url: jdbc:mysql://localhost:3306/mysql2excel?serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false
    datasource.username: root
    datasource.password: 123456
    datasource.driver-class-name: com.mysql.cj.jdbc.Driver
      
### mysql2excel.sql

---
   + [/src/test/resources/mysql2excel.sql](https://github.com/joker-pper/mysql2excel/blob/master/src/test/resources/mysql2excel.sql)
---

## 命令

```
# 打包
mvn -DskipTests package

# 查看帮助
java -jar mysql2excel-1.0.0-SNAPSHOT.jar --help

# 导出xls
java -jar mysql2excel-1.0.0-SNAPSHOT.jar -data-source ../src/test/resources/db.properties -file-name test -excel-type xls

# 导出xlsx
java -jar mysql2excel-1.0.0-SNAPSHOT.jar -data-source ../src/test/resources/db.properties -file-name test -excel-type xlsx

# 导出xls (指定路径)
java -jar mysql2excel-1.0.0-SNAPSHOT.jar -data-source ../src/test/resources/db.properties -out-path out -file-name test -excel-type xls 

# 导出xls (指定table - user) 
java -jar mysql2excel-1.0.0-SNAPSHOT.jar -data-source ../src/test/resources/db.properties -file-name test -excel-type xls -filter-table user 

# 导出xls (指定多个table - user role, 即只导入user role) 
java -jar mysql2excel-1.0.0-SNAPSHOT.jar -data-source ../src/test/resources/db.properties -file-name ../src/test/resources/test -excel-type xls -filter-table "user role" 

# 导出xls (排除table - user) 
java -jar mysql2excel-1.0.0-SNAPSHOT.jar -data-source ../src/test/resources/db.properties -file-name ../src/test/resources/test -excel-type xls -filter-table user -exclude 
```

