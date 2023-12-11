# Project

Course project for the
course [IDATA2304 Computer communication and network programming (2023)](https://www.ntnu.edu/studies/courses/IDATA2304/2023).

Project theme: a distributed smart greenhouse application, consisting of:

* Sensor-actuator nodes
* Visualization nodes

See protocol description in [protocol.md](protocol.md).

## Getting started

There are several runnable classes in the project.

To run the __central server__: run the `main` method inside the `CentralServerRunner` class.
This class need to be run in order for the two other runnable classes to run properly.

To run a __group of simulated field nodes__ connected to the central server: run the `main` method in the
`SimulatedFieldNodeSetupRunner` class. Note that these field nodes connect to the IP address defined in the same
class as the static field `IP_ADDRESS`, which is set to "localhost" by default. If you are running the central server
on a separate machine, please update this field accordingly.

To run the __control panel__ with __GUI__: run the `main` method inside the `ControlPanelRunner` class.
This will run a 'connector' application that asks you to provide an IP address for the server to connect to.
In order to be able to monitor the simulated field nodes, this IP address needs to be the same as the one
defined for the field nodes. Once you have connected to the central server, you can click the 'refresh' button
in the top right corner to view all available field nodes in the network.