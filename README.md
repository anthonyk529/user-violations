<h4>user-violations</h4>
===============
This Sonar plugin shows for a single project or for some projects the number of violations by developers and severity.
Furthermore, it allows seeing the evolution between two given dates.
it lists the developers' violations and allows bulk assignment.

Requirements : 
- Sonar 3.6

Steps to Install :
- build the plugin using  mvn clean install
- copy the .jar to the '$SONAR_HOME/extensions/plugin' directory
- restart the Sonar server