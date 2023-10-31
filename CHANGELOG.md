
# Cellolution - a simulated evolution of artificial cells in a water world: visible within the "ocean"

# Changelog



## v0.4.0

### Fixes

* None

### Features/Enhancements

* file persistence implementation for most  evolution simulation data (whole ocean), 
writing only


## v0.3.0

### Fixes

* Separate intermixture of sunbeam energy und sunshine brightness dependent on depth

### Features/Enhancements

* Changed JSON handling from json-simple v1.1.1 to JSON-java Release 20231013 due to
better handling in Cellolution and an added ordering within JSON objects and output
See: origin [JSON-java](https://github.com/stleary/JSON-java) and JSON-java with additions
of ordering within this repo under src/org/json 

* improved error handling on simulation file errors

* user may stop the simulation by starting a new one

* started file persistence implementation for the application and the evolution simulation data (smokers)


## v0.2.0

### Fixes

build.xml: JSON jar (simple JSON) classpath fix in certain circumstances (empty JSON file)

### Features/Enhancements

* Better decomposing of dead organisms, reuse of organic matter from the walls and ground


## v0.1.3

### Fixes

* Avoid ArrayIndexOutOfBoundsException (index = -1) if a replicating cell is at the surface (row = 0) and the evolutionary split leads to above right or above left.

### Features/Enhancements

* upload of 1st Github version (34 classes, ~6800 loc)
* first official release named "First Dive"
* added: create a new ocean
* updates to descriptive files




