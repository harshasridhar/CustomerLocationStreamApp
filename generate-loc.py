from random import random, randint


def is_direct_node(node):
    if node in [2, 4, 6, 8]:
        return True
    else:
        return False


def get_next_node(current):
    random_number = random()
    # print(random_number)

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


count = 0
current = randint(0,8)
print(current, end=" ")
while count < 20:
    current = get_next_node(current)
    print(current, end=" ")
    if current == -1:
        break
    count += 1
'''

curl \
    -i -X PUT -H "Accept:application/json" \
    -H  "Content-Type:application/json" http://localhost:8083/connectors/source-csv-filepulse-00/config \
    -d '{
        "connector.class":"io.streamthoughts.kafka.connect.filepulse.source.FilePulseSourceConnector",
        "fs.scan.directory.path":"/tmp/kafka-connect/examples/",
        "fs.scan.interval.ms":"10000",
     "fs.scan.filters":"io.streamthoughts.kafka.connect.filepulse.scanner.local.filter.RegexFileListFilter",
        "file.filter.regex.pattern":".*\\.csv$",
        "task.reader.class": "io.streamthoughts.kafka.connect.filepulse.reader.RowFileInputReader",
        "offset.strategy":"name",
        "skip.headers": "1",
        "topic":"customer-data",
        "internal.kafka.reporter.bootstrap.servers": "broker:29092",
        "internal.kafka.reporter.topic":"connect-file-pulse-status",
        "fs.cleanup.policy.class": "io.streamthoughts.kafka.connect.filepulse.clean.LogCleanupPolicy",
        "tasks.max": 1
    }'


'''