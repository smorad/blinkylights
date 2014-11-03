#!/bin/bash
if [ "$EUID" -ne 0 ]
	then
		echo You must run as root
		exit
fi
apt-get install python-dev
apt-get install libbluetooth-dev
wget https://pybluez.googlecode.com/files/PyBluez-0.20.zip
unzip PyBluez-0.20.zip
PyBlues-0.20/setup.py install
