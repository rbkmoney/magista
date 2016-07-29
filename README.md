## Magista query DSL

Для формирования запросов к данным сервис предоставляет DSL в JSON формате, который основан на Elasticsearch [Query DSL](https://www.elastic.co/guide/en/elasticsearch/reference/current/_introducing_the_query_language.html). 

Общий формат запроса выглядит следующим образом:

```json
{
  "query": { 
     "<query_type>": {
     	"<param>": "<val>"
    },
    "<query_param>":"<val>" 
}
```

`<query_type>` - тип запроса, который требуется выполнить. Параметры запроса зависят от от типа очереди.

`<query_param>` - параметр запроса, может включать:

1. `from` - (0-based) определяет, с какой записи результирующей выборки следует начать.
2. `size` - определяет, сколько максимум записей следует вернуть, начиная с `from`.

### Типы запросов
 Вся статистика считается за период времени и на данный момент может включать следующие типы запросов: 

1. `payments_turnover` - статистика по обороту.
2. `payments_geo_stat` - статистика по географии платежей.
3. `payments_conversion_stat` - статистика по конверсии.
4. `customers_rate_stat` - статистика по количеству плательщиков.

Также возможны запросы на выборку по моделям:

1. `payments` - выборка по платежам.
2. `invoices` - выборка по инвойсам.



### Параметры запросов статистики

Все запросы возвращают упорядоченный по `offset` набор ассоциативных массивов, состоящий из агрегированной информации по интервалам разбиения. Порядок вывода определяется значением  `offset` (смещение текущего агрегата относительно начала запрошенного временного диапазона), которое присутстует в каждом агрегате. В случае, если одному значению интервала соответствует более одного агрегата, они снабжаются дополнительными ключами для возможности их классификации(группировки).

Все типы запросов статистики должны содержать следующие параметры:

1. `merchant_id`- id мерчанта, для которого производится выборка.
2. `from` - начало временного интервала выборки.
3. `to` - конец временного интервала выборки.
4. `split_interval` - интервал разбиения выборки, по которому проводится агрегация, указывается в секундах.

Аргументы, содержащие время, должны быть представлены в формате ISO 8601 UTC.

##### `payments_turnover` 
Возвращает статистику по обороту (сумма успешных платежей за вычетом комиссий) в виде набора, сгруппированного по представленным в выборке валютам:

- `currency_symbolic_code` - символьный код валюты(ключ)
- `amount_with_commission` - сумма с вычетом комиссий
- `amount_without_commission` - сумма без вычета комиссий

##### `payments_geo_stat`
Возвращает статистику по географии платежей (агрегация сумм успешных платежей по городам) в виде набора, сгруппированного по представленным в выборке городам и валютам:

- `city_name` - город, который определился для данной совокупности платежей(ключ)
- `currency_symbolic_code` - символьный код валюты(ключ)
- `amount_with_commission` - сумма с вычетом комиссий
- `amount_without_commission` - сумма без вычета комиссий

##### `payments_conversion_stat` 
Возвращает статистику по конверсии (отношение количества успешых платежей к общему):

- `successful_count` - количество успешных платежей
- `total_count` - общее количество платежей
- `conversion` - конверсия (0 <= conversion <= 1)

##### `customers_rate_stat`
Возвращает статистику по количеству плательщиков (агрегация по уникальным плательщикам):

- `unic_count` - количество уникальных плательщиков

#### Пример
Запросим статистику для мерчанта с id=1 по гео-данным за час с интервалом разбиения в 5 минут:

```json
{
	"query": {
		"payments_geo_stat": {
			"merchant_id": 1,
			"from": "2016-03-22T00:12:00Z",
			"to": "2016-03-22T01:12:00Z",
			"split_interval": 300
		}
	}
}
```

В ответ получим структуру:

```json
[
	{
		"offset": 0,
		"city_name": "Москва",
		"currency_symbolic_code": "RUB",
		"amount_with_commission": "9000",
		"amount_without_commission": "10000"
	},
	{
		"offset": 0,
		"city_name": "Ярославль",
		"currency_symbolic_code": "RUB",
		"amount_with_commission": "900",
		"amount_without_commission": 1000
	},
	{
		"offset": 300,
		"city_name": "Москва",
		"currency_symbolic_code": "RUB",
		"amount_with_commission": 18000,
		"amount_without_commission": 19000
	},
	{
		"offset": 300,
		"city_name": "Ярославль",
		"currency_symbolic_code": "RUB",
		"amount_with_commission": 5000,
		"amount_without_commission": 5100
	}
]
```

### Параметры запросов моделей
Все запросы возвращают упорядоченный набор данных, отсортированных по времени создания.

##### `payments` 
Выборка по платежам. Параметры:

- `payment_id`
- `invoice_id`
- `from_time`
- `to_time`
- `payment_status` - статус платежа, имена берем из thrift-интерфейса **domain.InvoicePaymentStatus**, к примеру: `succeeded`
- `pan_mask` - маска в формате [\d\*]+ где все символы `\*` трактуются как любой символ
##### `invoices` 
Выборка по инвойсам. Параметры:

- `invoice_id`
- `from_time`
- `to_time`
- `invoice_status` - статус инвойса, имена берем из thrift-интерфейса **domain.InvoiceStatus**, к примеру: `paid`
