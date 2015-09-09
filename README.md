## Installation
It is assumed that if you are here, you are interested in *developing* (not just *using*) AODN custom talend components.  The easiest way to get started is to use the [project officer's vagrant machine](https://github.com/aodn/chef/blob/master/doc/README.examples.md#po-box), as follows.

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

