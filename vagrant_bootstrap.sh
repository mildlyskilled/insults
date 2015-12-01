#!/usr/bin/env bash

clear
echo "\n--- Ladies and Gentlemen, hold on to your shirts ---"
echo "\n--- we're building insult sword fighting mini cluster... ---"

echo "\n--- Updating packages list ---\n"
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823

apt-get update
apt-get -y autoremove
apt-get -y autoclean

apt-get install -y language-pack-en screen python-software-properties sbt nodejs

echo "------------ Equip Java -------------"
wget --no-check-certificate https://github.com/aglover/ubuntu-equip/raw/master/equip_java8.sh && bash equip_java8.sh

rm equip_*

echo "----------------- Export Node JS path ------------"
echo 'export SBT_OPTS="${SBT_OPTS} -Dsbt.jse.engineType=Node -Dsbt.jse.command=$(which node)"' >> ~/.bashrc

echo "\n------ PROVISIONING COMPLETE ------"
echo "--- To run this application vagrant ssh and then run ./activator run ---"
echo "\n-----------------------------------"
