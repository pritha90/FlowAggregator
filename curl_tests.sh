curl "http://localhost:8080/flows?hour=1"
curl "http://localhost:8080/flows?hour=2"

curl -X POST "http://localhost:8080/flows" -H 'Content-Type: application/json' -d '[{"src_app": "foo", "dest_app": "bar", "vpc_id": "vpc-1", "bytes_tx":300, "bytes_rx": 400, "hour": 1}]'
curl -X POST "http://localhost:8080/flows" -H 'Content-Type: application/json' -d '[{"src_app": "foo", "dest_app": "bar", "vpc_id": "vpc-1", "bytes_tx":300, "bytes_rx": 800, "hour": 1}]'

curl "http://localhost:8080/flows?hour=1"

curl -X POST "http://localhost:8080/flows" -H 'Content-Type: application/json' -d '[{"src_app": "foo", "dest_app": "bar", "vpc_id": "vpc-1", "bytes_tx":300, "bytes_rx": 800, "hour": 1}]'
curl -X POST "http://localhost:8080/flows" -H 'Content-Type: application/json' -d '[{"src_app": "foo", "dest_app": "bar", "vpc_id": "vpc-10", "bytes_tx":300, "bytes_rx": 800, "hour": 1}]'
curl -X POST "http://localhost:8080/flows" -H 'Content-Type: application/json' -d '[{"src_app": "foo", "dest_app": "bar", "vpc_id": "vpc-1", "bytes_tx":300, "bytes_rx": 800, "hour": 2}]'
curl -X POST "http://localhost:8080/flows" -H 'Content-Type: application/json' -d '[{"src_app": "foo", "dest_app": "bar", "vpc_id": "vpc-1", "bytes_tx":300, "bytes_rx": 800, "hour": 2}]’

curl "http://localhost:8080/flows?hour=1"
curl "http://localhost:8080/flows?hour=2"


curl -X POST "http://localhost:8080/flows" -H 'Content-Type: application/json' -d '[{“src_app":"foo","dest_app":"bar","hour":"1","bytes_tx":300,"vpc_id":"vpc-10","bytes_rx":800},
{"src_app":"foo","dest_app":"bar","hour":"1","bytes_tx":1200,"vpc_id":"vpc-1","bytes_rx":1600}]'
