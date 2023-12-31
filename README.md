<img src="https://github.com/openworld42/Cellolution/blob/main/src/cellolution/images/LogoReadMe.png" 
alt="Cellolution" align="right" style="right:40px; top:18px; width:400px; border:none;" />

<br />
<br />
<br />
<br />
<br />

# Cellolution

<h3>Watch a simulated evolution of cells and organisms in a GUI showing an artificial ocean.</h3>

[![Maintenance Status](https://badgen.net/badge/maintenance/active/green)](https://github.com/openworld42/Cellolution#maintenance-status)
![dependencies](https://img.shields.io/badge/dependencies-none-orange)
[![License](https://badgen.net/badge/issue/active/blue)](https://github.com/openworld42/Cellolution/issues)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://makeapullrequest.com) 

**What is Cellolution? It's a mélange of a game, a simulation, a screensaver and an evolutionary process in its own small universe: the Ocean.**

Within this ocean there are different kinds of single cells and more complex organisms (complex evolving organisms are under construction, some smaller ones available within the next days or weeks).

The ocean has dissolved materials as well as energy (beams of sunlight, organic matter, H2S hydrogen sulfide, and others) to let to organisms grow, starve or die. If the energy and material of an organism reaches a certain level, it splits itself into two of them - sometimes with an evolutionary transformation to another type of species.

**Interested? Look at these examples:**

"Black Smokers"  exists on the ground of the ocean emitting H2S and sometimes H2S consuming cells. 
<p align="center">
  	<img src="https://github.com/openworld42/Cellolution/blob/main/readme.images/Screenshot_Smoker1.png?raw=true" title="Black Smoker">
</p>


<br/>"Algae Cells" like CO2 and sunlight as energy source, therefore live usually close to the surface. All organisms have (at least) a random Brownian motion, like all small particles in a natural environment.
<p align="center">
  	<img src="https://github.com/openworld42/Cellolution/blob/main/readme.images/Screenshot_AlgaeCells1.png?raw=true" title="Algae Cells">
</p>


<br/>If an organism has accumulated enough energy (and some substances), it may split up into two organisms - sometimes with an evolutionary transformation: 
<p align="center">
  	<img src="https://github.com/openworld42/Cellolution/blob/main/readme.images/Screenshot_Split1.png?raw=true" title="Split 1">   
  	 &nbsp; &nbsp; &nbsp; or close to the surface &nbsp; &nbsp; &nbsp;	
  	<img src="https://github.com/openworld42/Cellolution/blob/main/readme.images/Screenshot_Split2.png?raw=true" title="Split 2">
</p>


<br/>
Cells may also starve (color: dark red), lacking of energy and/or substances. If starvation continues, the cell will die. Dead cells (black) are sinking to the ground and their matter disintegrates within the ocean again - until they vanish.
<p align="center">
  	<img src="https://github.com/openworld42/Cellolution/blob/main/readme.images/Screenshot_StarveAndDie.png?raw=true" title="Starving and dead cells">
</p>

<br/>
<p>
All simulation data are stored in JSON files, therefore several simulations can be maintained.
</p>

Needless to say, there are more things to discover. And (about patience): you need to give Cellolution some time to let the organisms do their evolution.

### How to run it:
Download the newest Github release **cellolution_vx.x.x.jar** file into a new directory (suggestion: "Cellolution"), cause it will save its state in JSON files. Call it from the command line within this directory or create a starter/menu item using

**java -jar cellolution_vx.x.x.jar**

where **x.x.x** is the current version. You need a Java runtime/JDK installed (at least version 17 - check on command line using **java -version**).<br/>
To get it: **Linux**: simply use your package manager, **Windows/macOS/others**: download and install JDK from [here](https://openjdk.java.net/).<br/> 


You may also build it from scratch using **Ant** and the **build.xml** file.<br/>

**Apache 2.0 licensed**. Each other license for built-in or integrated repos, projects, resources, icons, pictures, files etc. is found in 
[LICENSE.integrated](https://github.com/openworld42/Cellolution/blob/main/LICENSE.integrated). <br/>

**Credits, Kudos and Attribution:** 
Cellolution uses [JSON-java](https://github.com/stleary/JSON-java) with some modifications (no dependencies).

**Author:** Heinz Silberbauer (You like it? Spend a Github Star to motivate me :whale:)

Contributions (or a request :slightly_smiling_face:) from any interested party are welcome - please open an issue with a short description.




