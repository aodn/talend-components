## This repository

This repository contains the source code for Talend custom components used by the AODN.

## What are talend custom components?

In addition to the 800+ components that ship with Talend Studio, Talend allows users to create their own components for 
use when creating ETL jobs.

## Writing a custom component

The following documentation was used to write the current set of components (for version 5.0)

* [Writing custom components](https://www.talendbyexample.com/talend-custom-component-writing.html)

## Custom build process

We use a custom process for defining and building components for Talend installation developed when we were working with
Talend 5.  Talend 5 did not provide any support for bundling supporting java code and managing dependencies.   We used 
maven to provide this support.  Talend moved to a maven process as well in version 7 (a different maven process) but we 
have not yet migrated to using that process.

## Using the maven project

The maven project consists of sub modules for each component to be built and two sub modules (component-packager and directory-build)
used to build the user component directory containing components as described above for installation in Talend Open Studio.

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
The easiest way to get started is to use the [project officer's vagrant machine](https://github.com/aodn/chef/blob/master/doc/README.examples.md#po-box), as follows.

Assuming you have cloned and are in the root of the `chef` repo:

```bash
# Get the source code.
git clone git@github.com:aodn/talend-components.git src/talend-components
cd src/talend-components
mvn clean test install
cd -

# Start the PO VM.
bin/po-box.sh

# Run talend
bin/talend.sh
```

Once talend is up and running, set both of:

* 'Window' menu > Preferences > Talend > Components > User component folder
* 'Window' menu > Preferences > Talend Component Designer > Component Project

to `/vagrant/src/talend-components/directory-build/target/talend-components`.

To build and test a change to a component's source code:

1. `mvn install` from the `talend-components` directory
1. Switch to the `Component's Designer` perspective
1. Show view `Component Designer`
1. Right-click on the component in the `Component Designer` view, then click `Refresh`
1. Right-click on the component in the `Component Designer` view, then click `Push Components to Palette`

You should now be able to run the job with the updated component.  Simple.


