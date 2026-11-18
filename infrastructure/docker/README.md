# PostgreSQL runs on port 5432.
# Kafka runs on port 9092.
# Kafka uses KRaft mode, so we don't need ZooKeeper.
# Kafka has two listeners: localhost:9092 for applications running locally and kafka:29092 for applications running inside Docker.
# PostgreSQL uses a separate database for Auth Service. Other services will own their own databases.