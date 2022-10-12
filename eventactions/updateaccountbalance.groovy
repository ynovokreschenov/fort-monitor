def fmAccount = subject.fmAccount
def fmAccountNumber = fmAccount.fmAccountNum
def balance = subject.fmBalance
def description = "Добавление из платежа ITSM365 ${subject.title}"

def sid = modules.fortmonitor.connect().result
def accountInfo = modules.fortmonitor.httpRequest(
  method='get',
  path='updateaccountbalance',
  query=[
    'accountNumber':fmAccountNumber,
    'balance':balance,
    'description':description
    ],
  body = [:],
  headers=[
    'SessionId':sid
    ]
  ).result

modules.fortmonitor.disconnect()

utils.event(subject, "Результат синхронизации: ${accountInfo}");