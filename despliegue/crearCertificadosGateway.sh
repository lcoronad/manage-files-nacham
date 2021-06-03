echo "
[ req ]
req_extensions     = req_ext
distinguished_name = req_distinguished_name
prompt             = no

[req_distinguished_name]
commonName=apps.ocp.ach.rhcol.org

[req_ext]
subjectAltName   = @alt_names

[alt_names]
DNS.1  = apps.ocp.ach.rhcol.org
DNS.2  = *.apps.ocp.ach.rhcol.org
" > certificados/cert.cfg

openssl req -x509 -config certificados/cert.cfg -extensions req_ext -nodes -days 730 -newkey rsa:2048 -sha256 -keyout certificados/tls.key -out certificados/tls.crt

oc login --server=https://api.ocp.ach.rhcol.org:6443 --username=kubeadmin --password=NsM3A-kLHGW-HLQyY-KveHj --insecure-skip-tls-verify=true

oc create secret tls istio-ingressgateway-certs --cert certificados/tls.crt --key certificados/tls.key -n istio-system

oc patch deployment istio-ingressgateway -p '{"spec":{"template":{"metadata":{"annotations":{"kubectl.kubernetes.io/restartedAt": "'`date +%FT%T%z`'"}}}}}' -n istio-system
