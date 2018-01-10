# try-azure
Executed with these two Maven commands:
mvn clean package -Dmaven.test.skip=true

mvn exec:java -Dexec.mainClass=com.testadls.App -Pdirect-runner -Dexec.args="--runner=DirectRunner"