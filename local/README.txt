When running 2 servers on the same machine, you might have to add the a loopback route using to make discovery for hornetq work:

	sudo route add 224.0.0.0 127.0.0.1 -netmask 240.0.0.0