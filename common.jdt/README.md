## lspcommon.jdt
A common [Eclipse JDT LS](https://github.com/eclipse/eclipse.jdt.ls) Extension plugin for Eclipse. This plugin is designed to work in conjunction with a specific flavor (microprofile, jakarta etc) of a java language LS JDT Extension plugin. It contains centralized common functions shared by all Language servers providing specialized language support for java files.

This project is built using Eclipse Tycho (https://www.eclipse.org/tycho/) and requires at least maven 3.0 (http://maven.apache.org/download.html) to be built via CLI. 

Simply run :
```
    mvn install
```
in each of the two sub-components, begining with `org.eclipse.lspcommon.jdt.core` and followed by `org.eclipse.lspcommon.jdt.site`

### Components 

- [org.eclipse.lspcommon.jdt.core](./org.eclipse.lsp4jakarta.jdt.core) 
    - Common-ized Eclipse JDT-LS plug-in
- [org.eclipse.lspcommon.jdt.site](./org.eclipse.lsp4jakarta.jdt.site) 
    - Eclipse update site project
    - Creates a zipped p2 repository in the `/target` directory

Install the plugin via the zipped p2 repository into eclipse to satisfy compile / integration points for the jakarta LS.

An implementation of a slim jdt extension plugin that depends on or uses this plugin for its mainline functionality (completion, diagnostics, codelend etc) can be found here: [LSP4Jakarta Language Server using common component](https://github.com/mezarin/lsp4jakarta-test/tree/makeUseOfCommonPluginPOC)

Steps to run the Jakarta Language Server found in the above branch with this common jdt extension plugin:
- Build this plugin into a p2 repository following the direction above
- In your development/parent Eclipse, import the Jakarta LS code from the branch listed above
- Install this plugin into your development/parent Eclipse where you imported the Jakarta LS branch code
- There should not be any src compile errors in the following components:
    - jakarta.eclipse/org.eclipse.lsp4jakarta.lsp4e.core
    - jakarta.jdt/org.eclipse.lsp4jakarta.jdt.core
    - jakarta.ls
- There may be errors detected in the pom.xml files related to tycho warnings - they will not prevent you from running a child eclipse
- Right click on jakarta.eclipse/org.eclipse.lsp4jakarta.lsp4e.core and choose 'Run As -> Eclipse Application'
- A child eclipse will launch with the neccessary plugins and elements installed into it for Jakarta LS
- Import a jakarta based application and hit 'Ctrl-Space' in a java part and you will see a list of jakarta specific completions/snippets in the resulting popup window.

See more information about the Tycho packaging types: https://wiki.eclipse.org/Tycho/Packaging_Types. 
