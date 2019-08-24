def jenkins = jenkins.model.Jenkins.instance

branch = "master"

git_url = "https://github.com/sspatil05/MyProject"

 

freeStyleJob("sachin-test") {

    description 'Build job for Hybris'

    logRotator(daysToKeep = -1, numToKeep = 15, artifactDaysToKeep = -1, artifactNumToKeep = -1)

    label('devl-slave')

 

          scm {

        git {

            remote {

                url("${git_url}")

                credentials('hcs-infra-github-token')

            }

            branch("${branch}")

            extensions {

            }

        }

 

    }

    parameters {

            choiceParam("HYBRIS_COMMERCE_SUITE", ["HYBRISCOMM6300P_7-70002554.zip","HYBRISCXCOMM181100P_5-70004085.zip","HYBRISCOMM6500P_3-80003045.zip"])

            stringParam('HYBRIS_OPT_CONFIG_DIR','${WORKSPACE}/hybris/env/ci')

                   choiceParam("branch", ["hybrislighty","hybrisaws"])

                  

    }

 

    wrappers {

      preBuildCleanup()

      maskPasswords()

     

    }

    steps {

        shell('''

ant -version

             ''')

    }

 

    steps {

        ant {

            

            targets(['install'])

            props('environment': 'ci','hybris.zip.package.src': '../hcs_57_hybris_install_media/${HYBRIS_COMMERCE_SUITE}')       

            antInstallation('Ant 1.9')

           

        }

        ant {                             

            targets(['clean', 'customize', 'all', 'production'])

            props('environment': 'ci','hybris.zip.package.src': '../hcs_57_hybris_install_media/${HYBRIS_COMMERCE_SUITE}')       

            

        }

    }

 

 

    steps {

        shell('''

zip -j deere-hybris.zip hybris/temp/hybris/hybrisServer/*.zip

cd hybris/temp/hybris/hybrisServer/

unzip hybrisServer-solr.zip

 

#moving the custom configrations from "hybris/config/solr/configsets/"

#cp -rf /var/lib/jenkins/workspace/hcs_57/hcs_57_build/hybris/config/solr/configsets/ /var/lib/jenkins/workspace/hcs_57/hcs_57_build/hybris/bin/ext-commerce/solrserver/resources/solr/server/solr/

 

cp -rf /var/lib/jenkins/workspace/hcs_57/hcs_57_build/hybris/config/solr/configsets/ /var/lib/jenkins/workspace/hcs_57/hcs_57_build/hybris/temp/hybris/hybrisServer/hybris/bin/ext-commerce/solrserver/resources/solr/server/solr/

mv hybris/bin/ext-commerce/solrserver/resources/solr .

 

#rm -r hybris

tar -czvf deere-solr.tgz solr

#rm -r solr

             ''')

    }

 

}
