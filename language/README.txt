
1) Define the code for your language, this code must be like xx-XX.

2) Go to language directory (where build.xml is) and execute "ant extract".
   The target extract will extract all ApplicationResources.properties from other
   projects and copy them to [OpenVPN-ALS_LANGUAGE_PROJECT_DIR]/src/code.lang.

3) Translate properties files through Omega-T, for each properties file Omega-t
   will create the translated properties with a suffix
   ApplicationResources_xx.properties where xx is the first letters of code
   language defined in Step 1. If the translated file name doesn't contains these
   characters you must rename it.

   The translated property file must be in the same directory of the source.
 
Example:
     
     If in step 1) I define code for french : "fr-FR", the task in step 2 will
     create a new Directory "fr-FR" in [OpenVPN-ALS_LANGUAGE_PROJECT_DIR]/src/. This
     directory contains all ApplicationResources.properties found in OpenVPN-ALS
     (with directory structure). Now suppose, I want to translate Installation
     part of openvpnals, I have to load in Omega-T this file:

     [OpenVPN-ALS_LANGUAGE_PROJECT_DIR]/src/fr-FR/net.openvpn.als/install/ApplicationResources.properties
     
     OmegaT will create a properties file in target directory. You must copy it
     (and possibly rename it) to [OpenVPN-ALS_LANGUAGE_PROJECT_DIR]/src/fr-FR/net.openvpn.als/install/.
     In this case the translated file will be ApplicationResources_fr.properties.

     Note: you can delete or keep the source property file.

     The ant build script only keep properties file named ApplicationResources_aa.Properties
     where aa is the first letters of code language defined in Step 1.

     Now we can test the extension language. To build a language extension zip-file you have
     to call an ant task in [OpenVPN-ALS_LANGUAGE_PROJECT_DIR]:

      ant dist-language

     This task need 3 arguments: language code, language name that will be display in combo
     box in Web UI and a license definition.
 
     Example for french:

       ant -Dcode.lang=fr-FR -Dlanguage.name=Fran&#231;ais -Dlicense="GPLv2" dist-language

     the extension zip is created in [OpenVPN-ALS_LANGUAGE_PROJECT_DIR]. You can copy it to
     [OpenVPN-ALS_BIN]/conf/repository/archives or upload it via the WebUI. After that you can
     select the default language from OpenVPN-ALS's server settings. It's also possible to
     change the language on a per-user basis by granting user(s) the Change language
     permission.



    




