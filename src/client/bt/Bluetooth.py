#!/usr/bin/python
#This script is meant to run on the client (PC)
#It will read in a JSON objects of arrays output by the Java program
#and send them over to the bluetooth server (WIP)

import bluetooth
import sys
import json
import argparse
import fileinput

TARGET_NAME = "LED Orb Server"

def find_dev_addr(target_name):
	"Returns bt address of orb"
	for addr in bluetooth.discover_devices():
		if bluetooth.lookup_name(addr) == target_name:
			target_address = addr
			return addr
	return None

def bt_send(data, addr, port=1):
	"Sends data over bt to addr"
	sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
	sock.connect((addr, port))
	sock.send(data)
	sock.close()

def deserialize(fname):
	"Deserialize JSON"
	with open(fname, "r") as fp:
		with fp.read() as json_str:
			return json.loads(json_str)

def main():
	parser = argparse.ArgumentParser(description="Send data over bluetooth")
	parser.add_argument('-f', nargs='?', 
		help='Give it a filename of a JSON formatted text file instead of piping to it.',
 		type=str)
	args = parser.parse_args()
	if args.f:
		data = deserialize(args.f)
	else:
		#read from stdin, pipe me shit
		data = fileinput.input()
	dev_addr = find_dev_addr(TARGET_NAME)
	bt_send(data, addr, port)
	
if __name__ == "__main__":
	main()
