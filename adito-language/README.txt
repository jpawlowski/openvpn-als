
1) define the code for your language, this code must be like xx-XX,

2) you go in build.xml in [ADITO_LANGUAGE_PROJECT_DIR]
   and execute the target : extract (ant extract). The target extract will extract all ApplicationResources.properties from other projects
   and copy them to [ADITO_LANGUAGE_PROJECT_DIR]/src/code.lang.

3) translate properties files through Omega-T, for each properties file Omega-t will create the translated properties with a suffix ApplicationResources_xx.properties
   where xx is the first letters of code language defined in Step 1. If the translated file name doesn't contains this chars you must rename it.
   The translated property file must be in the same directory of the source.
 
Ex : if in step 1) I define code for french : "fr-FR"
     the task in step 2 will create a new Directory "fr-FR" in [ADITO_LANGUAGE_PROJECT_DIR]/src/
     this directory contains all ApplicationResources.properties found in other project (with directory structure).
     now suppose, I want to translate Installation part of adito, i have to load in Omega-T this file :
     [ADITO_LANGUAGE_PROJECT_DIR]/src/fr-FR/com/adito/install/ApplicationResources.properties
     OmegaT will create a propertie file in target directory. you must copy it (and necessarily rename it )
     [ADITO_LANGUAGE_PROJECT_DIR]/src/fr-FR/com/adito/install/
     in this case the translated file will be ApplicationResources_fr.properties

     Note : you can delete or keep the source property file.

     The ant build script only keep properties file named
     ApplicationResources_aa.Properties where aa is the first letters of code language defined in Step 1.

     Now we can test the extension language,
     to build a zip extension language you have to call task in [ADITO_LANGUAGE_PROJECT_DIR]/build.xml
     task name : dist-language
     this task need 2 arguments, first the language code (same in step 1), and two the language name that will be display in combo box in Web UI.
     Example for french : ant -Dcode.lang=fr-FR -Dlanguage.name=Fran&#231;ais dist-language

     the extension zip is created in [ADITO_LANGUAGE_PROJECT_DIR] , you can copy it to [ADITO_BIN]/conf/repository/archives
     and try to install adito or run existing adito server, you can choose your new language




    




