.l2:{[book]
    `side`price xdesc 0!select sum qty by side, price: 0.5 xbar price from orders where sym=book
 }