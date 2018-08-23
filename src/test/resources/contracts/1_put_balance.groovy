package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        url '/player/1/balance'
        body([
                balance: "5"
        ])
        headers {
            contentType('application/json')
        }
    }
    response {
        status 200
        body([
                balance: "10"
        ])
        headers {
            contentType('application/json')
        }
    }
}