
# Build
Perform these steps if you want to build the app and push to your own docker

`docker login`

***replace yadneshchonkar with your docker image prefix***

`mvn install -Denv.docker.image.prefix=yadneshchonkar`
  

## Pom.xml issue

***TODO FixME for now run these below commands **

`docker image rm yadneshchonkar/sample_for_k8s`

`mvn dockerfile:build -Denv.docker.image.prefix=yadneshchonkar`

`docker push yadneshchonkar/sample_for_k8s`

  

# Run Docker

`docker run -p 8091:8091 -i -t yadneshchonkar/sample_for_k8s`
  

# URLS to use
Tweak the loopSize value as needed
`http://localhost:8091/spikeCPU?loopSize=10`

Tweak the words and length value as needed
`http://localhost:8091/spikeMemory?words=10&length=10`

Tweak the words and time value (in secs) as needed
`http://localhost:8091/sleep?time=10`

  
## Actuator URLs
`http://localhost:8091/health`

`http://localhost:8091/metrics`

`http://localhost:8091/info`

`http://localhost:8091/trace`