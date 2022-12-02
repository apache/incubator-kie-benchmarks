#[condition][]There is a customer with name "{lastName}"=customer : Customer(lastName == lastName)
[consequence][]Log "{message}"=//System.out.println("{message} "); 

[when]There is a Address with=address: Address()
[when]- street "{street}"=street=="{street}"
[when]- city "{city}"=city=="{city}"
[when]- postal code "{postCode}"=postCode=="{postCode}"

[then]This is great street!=address.setStreet(address.getStreet() + "greatStreet!");update(address);
[then]Street is no longer valid. Delete it.=address.setStreet("");update(address);

[when]There is a Customer with=customer: Customer()
[when]- first name "{firstName}"=firstName=="{firstName}"
[when]- last name "{lastName}"=lastName=="{lastName}"
[when]- above address=address=="{address}"

[then]Customer is bad guy=customer.setLastName(customer.getLastName() + "BadGuy");update(customer);
[then]Customer is good guy=customer.setLastName(customer.getLastName() + "GoodGuy");update(customer);
[then]Customer is from Moravia=customer.setLastName(customer.getLastName()+ "jay!");update(customer);

[when]There is a new customer=NewCustomerEvent()
