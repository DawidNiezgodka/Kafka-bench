import csv
import random
import time

def generate_random_word(word_length):

    return ''.join(random.choices('abcdefghijklmnopqrstuvwxyz', k=word_length))

def generate_row(index, target_row_size):

    ts = int(time.time() * 1000)
    a = random.randint(1000000, 9999999)
    b = random.randint(1000000, 9999999)
    c = random.randint(1000000, 9999999)
    d = random.randint(1000000, 9999999)
    e = random.randint(1000000, 9999999)
    f = random.randint(1000000, 9999999)
    g = random.randint(1000000, 9999999)
    h = random.randint(1000000, 9999999)
    res = random.randint(1000000, 9999999)

    row = [ts, index, a, b, c, d, e, f, g, h, res]
    row_str = ','.join(map(str, row))
    current_size = len(row_str.encode('utf-8'))
    remaining_size = target_row_size - current_size - 10
    if remaining_size > 0:
        for i in range(5, 10):
            word_length = random.randint(1, remaining_size // (10 - i))
            row[i] = generate_random_word(word_length)
            remaining_size -= word_length
            if remaining_size <= 0:
                break

    return row

def create_csv_file(file_name, num_rows, row_size_range):
   with open(file_name, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow(['ts', 'index', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'res'])

        for i in range(num_rows):
            target_row_size = random.randint(*row_size_range)
            row = generate_csv_row(i, target_row_size)
            writer.writerow(row)
csv_file_name = 'random_data.csv'
create_csv_file(csv_file_name, 10000, (800, 1000))
