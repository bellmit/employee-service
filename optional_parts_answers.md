**Second Part (Optional but a plus):**

Being concerned about developing high quality, resilient software, giving the fact, that you will be participating, mentoring other engineers in the coding review process.


- Suggest what will be your silver bullet, concerns while you're reviewing this part of the software that you need to make sure is being there.
- What the production-readiness criteria that you consider for this solution

there is several aspects that come in mind when reviewing other particpatents code:

1- Consistency:
    - system should prevent going into an invalid states giving a meaningful error for that and guiding the user into completing the state machine process.
    for example: employe should go from ADDED -> INCHECk -> APPROVED -> ACTIVATED in order and anything outside this order should give an error.
    
2- Exception handling:
    - how the system handles exception giving proper http status codes and meaningful messages.
    
3- Flexibility:
    - is the system flexible enough to add new states or change the current behaviour (making the code as generic as possible and easier to add new states without altering the current business logic)
    
4- Clean code:
    - code shoulw be clean and readable for reviewing with eassy to understand logic.
    
5- Logging and response:
    - logging should be present in the system for me to easily track the system behaviour during the runtime.
    - response returned in http  should be meaningful and give good information in the success response.
    
**What the production-readiness criteria that you consider for this solution?**

Including the above points other points should be taken into consideration for the production readiness of the system.

- load testing:
    - we should know the  capabilite of the system and how many requests it could handle, and seek ways to improve performance if needed.
- concurrency:
    - concurrency should be taken into consideration of what will happen if several request of the same employee is recieved and how the system will behave.
    
-----------------------------------------------
**Third Part (Optional but a plus):**
Another Team in the company is building another service, This service will be used to provide some statistics of the employees, this could be used to list the number of employees per country, other types of statistics which is very vague at the moment.


- Please think of a solution without any further implementation that could be able to integrate on top of your service, including the integration pattern will be used, the database storage etc.

A high-level architecture diagram is sufficient to present this.

** Answer **

- the basic idea here is using async messaging to recieve employee event ( we can use apache kafka with it's competing  consumer AKA  paritions to increase parallelism and throughput.
- from there we can based on certain condition either using content based router or dynamic based content router, (e.g:  existing in apache camel) to determine the message destination.
- sent the message into another queue ( topic) which is consumed by a transformer that transform the message to be compatiable with the consuming service.
- recieve the message from the consuming service (e.g: statistics).
- we can easily add other systems to the mix with just a new topic and a transform for the new service.

some integration patterns might change for example if we needed the events to be sequencial we can only use one parition or change to apache rabiitMQ .

** Diagram can be found in hte same folder [Employee_Integration](Employee_Integration.png) **
