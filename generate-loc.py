from random import random, randint
from datetime import datetime, date
from confluent_kafka import Producer
from time import sleep


def is_direct_node(node):
    if node in [2, 4, 6, 8]:
        return True
    else:
        return False


def get_next_node(current):
    random_number = random()
    # print(random_number)

    if current == -1:
        if random_number <= 0.4:
            return 2*randint(1, 4)
        else:
            return current

    if current == 0:
        if random_number <= 0.4:
            return randint(1, 8)
        else:
            return current

    if is_direct_node(current):
        if random_number <= 0.05:
            return 0
        elif random_number <= 0.20:
            return current - 1 if current != 1 else 8
        elif random_number <= 0.40:
            return current
        elif random_number <= 0.65:
            return current + 1 if current != 8 else 1
        else:
            return -1
    else:
        if random_number <= 0.15:
            return current - 1 if current != 1 else 8
        elif random_number <= 0.35:
            return current
        elif random_number <= 0.65:
            return 0
        else:
            return current + 1 if current != 8 else 1


producer = Producer({'bootstrap.servers': 'localhost:9092'})
customer_location = {i: randint(0, 8) for i in range(1, 201)}
count = 0
while count < 2000:
    customer_id = randint(1, 200)
    current = customer_location[customer_id]
    current = get_next_node(current)
    customer_location[customer_id] = current
    message= {'CustomerID': customer_id, 'region': current, 'timestamp': str(datetime.now())}
    print(str(message))
    producer.produce("customer-location", key=str(customer_id), value=str(message))
    sleep(0.1)
    if customer_id % 10 == 0:
        producer.flush()
        sleep(0.5)
    count += 1
producer.flush()
