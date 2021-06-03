export PATH=$PATH:/home/lcoronad/Documentos/RedHat/programas/oc-4.3.3
export NAMESPACE_APP=ach-mft-integration
export NAME_APP=manage-files-nacham
export TAG_IMAGE=1.0.0

#Autenticarse en OCP
oc login --server=https://api.ocp.ach.rhcol.org:6443 --username=kubeadmin --password=NsM3A-kLHGW-HLQyY-KveHj --insecure-skip-tls-verify=true

#Ubicarse en el proyecto
oc project $NAMESPACE_APP

#Crear el configmap
oc create -f despliegue/configmap-manage-files-nacham.yml -n $NAMESPACE_APP

#Crear el secret
oc create -f despliegue/secret-manage-files-nacham.yml -n $NAMESPACE_APP

#Crear el build
oc new-build --binary=true --name=$NAME_APP openshift/fuse7-java-openshift:1.8 -n $NAMESPACE_APP

#Crea la aplicacion
oc new-app $NAMESPACE_APP/$NAME_APP:latest --name=$NAME_APP --allow-missing-imagestream-tags=true --as-deployment-config=true -n $NAMESPACE_APP

#Quitar el trigger para que que haga deploy por cambios en la configuración
oc set triggers dc/$NAME_APP --remove-all -n $NAMESPACE_APP

#Crear el service
oc expose dc $NAME_APP --port 8080 -n $NAMESPACE_APP

#Iniciar un build
oc start-build $NAME_APP --from-file=target/manage-files-nacham-$TAG_IMAGE.jar --wait=true -n $NAMESPACE_APP

#Tag de la imagen
oc tag $NAME_APP:latest $NAME_APP:$TAG_IMAGE-$NUM_BUILD -n $NAMESPACE_APP

#Actualizar deployment
oc apply -f despliegue/deployment.yaml

#Se actualiza la imagen en el deployment config
oc set image dc/$NAME_APP $NAME_APP=$NAMESPACE_APP/$NAME_APP:$TAG_IMAGE-$NUM_BUILD --source=imagestreamtag -n $NAMESPACE_APP

#Realizar el deploy
oc rollout latest dc/$NAME_APP -n $NAMESPACE_APP

#Exponer un ruta - se desactivo porque se va a usar el ingressGateway de ServiceMesh
#oc expose svc $NAME_APP -n $NAMESPACE_APP

#Crear los recursos de ServiceMesh (PeerAuthentication, VirtualService, DestinationRule)
oc create -f despliegue/service-mesh-ingress-internal-qualification-async.yaml -n $NAMESPACE_APP



curl --location --request POST 'http://localhost:8080/api/files-nacham/save-movement-file' \
--header 'Content-Type: application/json' \
--data '{
    "fileName": "pruebaOcc.txt",
    "fileDate": "2021-12-01",
    "fileState": "Inicio",
    "financialEntity": "Banco de Occidente"
}'

curl --location --request GET 'http://localhost:8080/api/files-nacham/get-file-state/2021-01-01/2021-05-10/Banco de Bogota'

curl --location --request GET 'http://localhost:8080/api/files-nacham/get-file-list/2021-01-01/2021-12-10'

curl --location --request GET 'http://localhost:8080/api/files-nacham/health-check'


