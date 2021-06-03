echo "---
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: wildcard-gateway
spec:
  selector:
    istio: ingressgateway # use istio default controller
  servers:
  - port:
      number: 443
      name: https
      protocol: HTTPS
    tls:
      mode: SIMPLE
      privateKey: /etc/istio/ingressgateway-certs/tls.key
      serverCertificate: /etc/istio/ingressgateway-certs/tls.crt
    hosts:
    - ingress-gateway.apps.adesaocp1.nh.inet
" > wildcard-gateway.yml

oc login --server=https://api.ocp.ach.rhcol.org:6443 --username=kubeadmin --password=NsM3A-kLHGW-HLQyY-KveHj --insecure-skip-tls-verify=true

oc create -f wildcard-gateway.yml -n istio-system


