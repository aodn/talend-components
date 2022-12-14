## This repository

This repository contains the source code for Talend custom components used by the AODN. 
### Licensing
This project is licensed under the terms of the GNU GPLv3 license.

## What are talend custom components?

In addition to the 800+ components that ship with Talend Studio, Talend allows users to create their own components for 
use when creating ETL jobs.

## Writing a custom component

The following documentation for Talend version 5 was used to write the current set of components.

* [Writing custom components](https://www.talendbyexample.com/talend-custom-component-writing.html)

## Custom build process

We use a custom process for defining and building components for Talend installation developed when we were working with
Talend 5.  Talend 5 did not provide any support for bundling supporting java code and managing dependencies.   We used 
maven to provide this support.  Talend moved to a maven process as well in version 7 (a different maven process) but we 
have not yet migrated to using that process.

## Using the maven project

The maven project consists of:

* a sub module for each component to be built, 
* a component-packager sub-module which defines how to assemble each component into a zip file, 
* a directory-build sub module that unpacks each component built by the component-packager into the user component build directory, and
* the parent project which defines common dependencies, configuration for the component-packager and the sub modules
  included in the project.

### Creating a component

To add a simple component containing source code as per the "Writing a custom component" section above:

* add a new submodule to the project to create/include the component in the build
  * create a directory for the component
  * add a pom.xml file for the component inheriting from the parent pom and including 
    invocation of the assembly step during build as per other components.
  * add the module to the list of modules included in the parent pom
  * include the sub module in the list of dependencies included in directory-build module 
    to ensure the component is included in the component directory during the build
* add the src code for the component to the src/main/component directory

Building the maven project will then include this new component into the component directory that is
built for installation in Talend Open Studio.

To build a component containing java code, resources, unit tests and dependencies, include src/main/java, src/main/resources and
src/test directories and dependencies in the component sub module as per a normal maven jar project.  This will build a jar
and include it and declared dependencies into the component folder for installation in talend.  You will still need to 
declare the jars to be used in the Component Descriptor file including the main jar file built (See
iUpdateIndex/src/main/component/iUpdateIndex_java.xml).
   

### Building components

It is assumed that if you are here, you are interested in *developing* (not just *using*) AODN custom talend components.
The easiest way to get started is to use the [project officer's pipeline box](https://github.com/aodn/chef/blob/master/doc/README.pipeline-box.md), as follows.

Assuming you have cloned and are in the root of the `chef` repo:

```bash
# Get the source code.
git clone git@github.com:aodn/talend-components.git src/talend-components
cd src/talend-components
mvn clean test install
cd -

# Start the PO VM.
bin/pipeline-box.sh

# Run talend
bin/talend7-pipeline.sh
```

Once talend is up and running, set the following:

* 'Window' menu > Preferences > Talend > Components > User component folder

to `/vagrant/src/talend-components/directory-build/target/talend-components`.

To build and test a change to a component's source code:

1. `mvn install` from the `talend-components` directory
1. set 'Window' menu > Preferences > Talend > Components > User component folder
to a directory not containing any components e.g. /tmp
1. click apply
1. set 'Window' menu > Preferences > Talend > Components > User component folder 
to `/vagrant/src/talend-components/directory-build/target/talend-components`.
1. click apply

You should now be able to run the job with the updated component.  Simple.

## Components development using the tests

Some tests require a database and Geonetwork. We use the PO pipeline box and stack to provide these.

Run the PO pipeline box with the following added:
  Schema: soop_co2
  watches: SOOP_CO2
  jobs: soop_co2

In the stack Geonetwork run the "Catalogue Portal for systest" harvester to obtain required metadata.


Get the talend-components repo so that it can be used in Talend via the PO pipeline box.
```
cd ~/git/chef  # Or your chef directory
git clone git@github.com:aodn/talend-components.git src/talend-components
cd src/talend-components
mvn clean test install
```

Run Talend 7
```
bin/talend7-pipeline.sh
Import and open the /vagrant/src/talend-components/tests/TALEND_COMPONENT_TESTS/ project
'Window' menu > Preferences > Talend > Components > User component folder:  /vagrant/src/talend-components/directory-build/target/talend-components
```

Change the context values in Talend to match your PO stack.

Execute the 'runAll 0.1' job (or any single job) to run tests.

If changes are made to /vagrant/src/talend-components/directory-build/target/talend-components then:

	1. cd ~/git/chef/src/talend-components 
	2. mvn install
	3. Reload the components in Talend
		b. Window → Preferences → Talend → Components -->User component folder
		c. Paste in  /tmp
		d. Apply
		e. Window → Preferences → Talend → Components -->User component folder
		f. Paste in /vagrant/src/talend-components/directory-build/target/talend-components
		g. Apply

## Component Catalogue

### Pipeline Integration

Components used to perform common pipeline processing.  Refer to 
[pipeline integration](doc/pipeline_integration.md) for some background on what these
components are used for and supporting tables. 

* [iUpdateIndex](iUpdateIndex/README.md)
* [iNewFileList](iNewFileList/README.md)
* [iModifiedFileList](iModifiedFileList/README.md)
* [iNewOrModifiedFileList](iNewOrModifiedFileList/README.md)
* [iDeletedFileList](iDeletedFileList/README.md)


### Schema versioning/upgrade

* [iPostgresqlDbUpdate](iPostgresqlDbUpdate/README.md)

### GeoNetwork

* [iUpdateSpatialExtent](iUpdateSpatialExtent/README.md)

### NetCDF

* [iNetCDFInput](iNetCDFInput/README.md)
* [iNetCDFGlobalAttribute](iNetCDFGlobalAttribute/README.md)
* [iNetCDFGlobalAttributes](iNetCDFGlobalAttributes/README.md)
* [iNetCDFVariable](iNetCDFVariable/README.md)
* [iNetCDFVariableAttribute](iNetCDFVariableAttribute/README.md)
