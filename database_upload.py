import string
import time

import pandas as pd
import psycopg2 as pg
from psycopg2 import extras

DB_HOST = 'localhost'
DB_USER = 'golden_user'
DB_PASSWORD = ''
DB_NAME = 'golden'

DF_COLUMNS = ['DBA Name',
              'Street Address',
              'City',
              'State',
              'Source Zipcode',
              'Location Start Date',
              'Location End Date',
              'Neighborhoods - Analysis Boundaries',
              'Business Location']
SAN_FRAN = 'SAN FRANCISCO'
LIKELY_MISSPELLINGS = {'S F', 'S FRANCISCO', 'S SAN FRAN', 'SAM FRANCISCO', 'SANC FRANCISCO', 'SF', 'SAN', 'SA',
                       'SN FRANCISCO', 'SNA FRANCISCO', 'SOUTH SAN FRANCISCO', 'TREASURE ISLAND  SAN FRANCISCO',
                       'TREASURE ISLAND'}
DB_COLUMNS = ['name', 'neighborhood_id', 'neighborhood_name', 'name', 'street_address', 'location_start',
              'location_end', 'coordinates']


def _get_connection():
    conn = pg.connect(
        host=DB_HOST,
        port=5432,
        dbname=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD
    )
    cur = conn.cursor()
    return conn, cur


def _commit_connection(conn, cur):
    conn.commit()
    cur.close()


def _run_query(value_tuples_list, i, total):
    query = 'with neighborhood as ' \
            '(insert into neighborhoods(name) ' \
            'values (%(neighborhood_name)s) ' \
            'on conflict (name) do update set name = excluded.name returning id, name) ' \
            'insert into businesses(neighborhood_id, neighborhood_name, name, street_address, location_start, location_end, coordinates) ' \
            'values ((select id from neighborhood), (select name from neighborhood), %(biz_name)s, %(street)s, date(%(loc_start)s), ' \
            'case when %(loc_end)s = \'0001-01-01\' then null else date(%(loc_end)s) end, %(coord)s) on conflict do nothing;'
    conn, cur = _get_connection()

    # faster that normal execute
    extras.execute_batch(cur, query, value_tuples_list)
    _commit_connection(conn, cur)
    print(f'Processing {i + 1} of {total} batch(es)')


def import_data(path):
    data = _load_csv(path)
    print('Total rows: {}'.format(len(data)))
    chunk_size = 500
    data_chunks = [data[i:i + chunk_size] for i in range(0, data.shape[0], chunk_size)]
    total = len(data_chunks)

    params = ['index', 'neighborhood_name', 'biz_name', 'street', 'loc_start', 'loc_end', 'coord']

    total_rows = 0
    for i, d in enumerate(data_chunks):
        value_tuples_list = []
        for row in d.itertuples():
            total_rows += 1
            row_data = {'neighborhood_name': None, 'biz_name': None, 'street': None, 'loc_start': None, 'loc_end': None,
                        'coord': None}
            for j in range(len(row)):
                row_data[params[j]] = row[j]
            value_tuples_list.append(row_data)

        _run_query(value_tuples_list, i, total)
    return total_rows


def _load_csv(path):
    def _hasNumber(text):
        try:
            int(text)
            return False
        except:
            try:
                int(text[0])
                return False
            except:
                return True

    def _remove_punctuations(text):
        for punctuation in string.punctuation:
            text = text.replace(punctuation, ' ')
        return text

    def _add_mispelled(city):
        if str(city).startswith('SAN F') and str(city) not in LIKELY_MISSPELLINGS:
            LIKELY_MISSPELLINGS.add(city)

    def _replace(mistake):
        if mistake in LIKELY_MISSPELLINGS:
            return SAN_FRAN

    def _extract_point(text):
        text = text.replace('POINT (', '').replace(')', '').replace(' ', ',')
        return text

    df = pd.read_csv(path)
    no_missing_locations = df[df['Business Location'].notnull()]
    data = no_missing_locations[DF_COLUMNS]
    data = data[data['State'].str.upper() == 'CA']
    data = data[data['City'].apply(isinstance, args=(str,))]
    data = data[data['City'].apply(_hasNumber)]
    data = data[data['City'].notnull()]
    data = data[data['State'].notnull()]
    data = data[data['Neighborhoods - Analysis Boundaries'].notnull()]

    data['City'] = data['City'].apply(_remove_punctuations).str.upper().str.strip()
    data = data[data['City'] != 'N A']
    data['State'] = data['State'].str.upper()
    data['Neighborhoods - Analysis Boundaries'] = data['Neighborhoods - Analysis Boundaries'].str.upper()
    data['City'] = data['City'].apply(_add_mispelled)
    data['City'] = data['City'].apply(_replace)
    data['Business Location'] = data['Business Location'].apply(_extract_point)
    data['Location End Date'] = data['Location End Date'].fillna('0001-01-01')

    return data[['Neighborhoods - Analysis Boundaries', 'DBA Name', 'Street Address', 'Location Start Date',
                 'Location End Date', 'Business Location']]


if __name__ == '__main__':
    start = time.time()
    total_rows = import_data('Registered_Business_Locations_-_San_Francisco.csv')
    end = time.time()
    total_time = end - start
    print(f'Finished inserting {total_rows} rows in {total_time} seconds')
