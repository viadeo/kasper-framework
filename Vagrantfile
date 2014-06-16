$script = <<SCRIPT
##########################
# Tools
##########################
apt-get -y update
apt-get install -y python-software-properties wget unzip
su vagrant -c 'touch ~/.bashrc'

## Install Java7
add-apt-repository -y ppa:webupd8team/java
apt-get -y update
/bin/echo debconf shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
apt-get -y install oracle-java7-installer oracle-java7-set-default

su vagrant -c 'echo "export JAVA_HOME=$( dirname $( dirname $( readlink -e $(which javac) ) ) )" >> ~/.bashrc'

## Gradle
cd /tmp
if [ ! -f '/tmp/gradle-1.10-bin.zip' ]; then
	wget http://services.gradle.org/distributions/gradle-1.10-bin.zip
fi
mkdir -p /opt
cd /opt
unzip -o /tmp/gradle-1.10-bin.zip
su vagrant -c 'echo "export PATH=/opt/gradle-1.10/bin:$PATH" >> ~/.bashrc'

##########################
## RabbitMQ
##########################

echo "deb http://www.rabbitmq.com/debian/ testing main"  | sudo tee  /etc/apt/sources.list.d/rabbitmq.list > /dev/null

wget http://www.rabbitmq.com/rabbitmq-signing-key-public.asc
apt-key add rabbitmq-signing-key-public.asc

apt-get update
apt-get install -q -y rabbitmq-server

## RabbitMQ Plugins
echo "[{rabbit, [{loopback_users, []}]}]." | sudo tee /etc/rabbitmq/rabbitmq.config > /dev/null

service rabbitmq-server stop
rabbitmq-plugins enable rabbitmq_management
service rabbitmq-server start
rabbitmq-plugins list


SCRIPT

Vagrant.configure("2") do |config|
  config.vm.box = "precise64"
  config.vm.box_url = 'http://files.vagrantup.com/precise64.box'
  config.vm.network "forwarded_port", guest: 55672, host: 55672
  config.vm.network "forwarded_port", guest: 5672, host: 5672
  config.vm.network "forwarded_port", guest: 15672, host: 15672
  config.vm.synced_folder ".", "/home/vagrant/src"
  config.vm.provision "shell", inline: $script
  # setting obveriding defaults for this vm
  config.vm.provider "virtualbox" do |vb|
     vb.customize ["modifyvm", :id, "--memory", "4096"]
     vb.customize ["modifyvm", :id, "--cpus", "2"]
  end
end
