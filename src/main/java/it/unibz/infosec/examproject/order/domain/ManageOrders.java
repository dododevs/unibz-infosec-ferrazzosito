package it.unibz.infosec.examproject.order.domain;

import it.unibz.infosec.examproject.product.domain.ManageProducts;
import it.unibz.infosec.examproject.product.domain.Product;
import it.unibz.infosec.examproject.user.domain.ManageUsers;
import it.unibz.infosec.examproject.user.domain.UserEntity;
import it.unibz.infosec.examproject.util.crypto.hashing.Hashing;
import it.unibz.infosec.examproject.util.crypto.rsa.RSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManageOrders {

    private OrderRepository orderRepository;
    private ManageUsers manageUsers;
    private ManageProducts manageProducts;
    private SearchOrders searchOrders;

    @Autowired
    public ManageOrders(OrderRepository orderRepository, ManageUsers manageUsers, ManageProducts manageProducts) {
        this.orderRepository = orderRepository;
        this.manageUsers = manageUsers;
        this.manageProducts = manageProducts;
    }

    private Order validateOrder (Long id) {

        Optional<Order> maybeOrder = orderRepository.findById(id);

        if(maybeOrder.isEmpty())
            throw new IllegalArgumentException("Order with id '" + id + "' does not exist yet!");

        return maybeOrder.get();
    }

    public Order createOrder(Long productId, Long clientId) {
        UserEntity client = manageUsers.readUser(clientId);
        Product product = manageProducts.readProduct(productId);

        String orderDocument =
                "ID: " + product.getId() + "\n" +
                "NAME: " + product.getName() + "\n" +
                "COST: " + product.getCost() + "\n" +
                "VENDOR: " + product.getVendorId() + "\n" +
                "BUYER: " + client.getId();

        String digest = Hashing.getDigest(orderDocument);
        byte[] DSA = RSA.encrypt(digest, client.getPrivateKey(), client.getNKey());

        return orderRepository.save(new Order(productId, clientId, orderDocument, DSA));
    }

    public Order approveOrder(Long idOrder) throws Exception {
        Order order = readOrder(idOrder);
        boolean orderValidity = isSignatureOrderValid(order.getId());

        if(!orderValidity)
            throw new Exception("Order digital signature is not valid!");

        order.setApproved(true);
        return orderRepository.save(order);
    }

    public boolean isSignatureOrderValid(Long idOrder) {
        Order order = readOrder(idOrder);
        UserEntity client = manageUsers.readUser(order.getClientId());
        byte[] DSA = order.getDSA();
        String retrievedDigest = RSA.decryptToString(DSA, client.getPublicKey(), client.getNKey());
        String computedDigest = Hashing.getDigest(order.getOrderDocument());

        return retrievedDigest.equals(computedDigest);
    }

    public Order readOrder(Long id) {

        Order order = validateOrder(id);

        return order;

    }

    public Order deleteOrder (Long id) {

        Order order = validateOrder(id);

        orderRepository.delete(order);

        return order;
    }

}
