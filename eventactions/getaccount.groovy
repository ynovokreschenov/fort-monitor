def fmAccount = subject
def fmAccountNumber = fmAccount.fmAccountNum

def sid = modules.fortmonitor.connect().result
def accountInfo = modules.fortmonitor.httpRequest(
  method='get',
  path='getaccount',
  query=[
    'accountNumber':fmAccountNumber
    ],
  headers=[
    'SessionId':sid
    ]
  ).result
modules.fortmonitor.disconnect()

if (accountInfo.Result == "Ok"){
  def objectParams = [
    'fmBalance':accountInfo.Balance,
    'fmCurrency':accountInfo.Currency,
    'fmDescription':accountInfo.Description,
    'fmEstimatedDay':accountInfo.EstimatedDays,
    'fmEstimatedInf':accountInfo.EstimatedInfo,
    'fmEstInfoExt':accountInfo.EstimatedInfoExtended,
    'fmAccountType':accountInfo.AccountType,
    'fmBalanceType':accountInfo.BalanceType,
    'fmBlockLimit':accountInfo.BlockLimit,
    'fmBlObOnBalDow':accountInfo.BlockObjectsOnBalanceDown,
    'fmBlUsOnBalDow':accountInfo.BlockUsersOnBalanceDown,
    'fmDataMonPrice':accountInfo.DataMonthPrice,
    'fmEmail':accountInfo.Email,
    'fmFortMonPrice':accountInfo.FortMonthPrice,
    'fmNotDaysCntBl':accountInfo.NotifyDaysCountBeforeBlocking,
    'fmShDebOnBlLog':accountInfo.ShowDebtOnBlockedLogon,
    'fmSmsCost':accountInfo.SmsCost,
    'fmMonSingPay':accountInfo.MonthSinglePayment,
    'fmPhoneNumber':accountInfo.PhoneNumber,
  ]
  utils.edit(fmAccount, objectParams)
}

/*
    "AccountNumber": "22222",
    "Balance": 105.0,
    "Company": "Naumen Тестовая компания 01",
    "Currency": "RUR",
    "Description": "",
    "EstimatedDays": 2147483646,
    "EstimatedInfo": "0!0!0",
    "EstimatedInfoExtended": "Число незаблокированных терминалов FORT: 0<br>Число незаблокированных терминалов не FORT: 0<br>Объектов заблокировано: 0<br>Объектов удалено: 0<br>Кол-во объектов с тарифом по умолчанию: 0<br>Кол-во объектов с индивидуальными тарифами: 0<br>Стоимость обслуживания за день: 0",
    "Id": 278,
    "AccountType": 1,
    "BalanceType": 1,
    "BlockLimit": 0.0,
    "BlockObjectsOnBalanceDown": false,
    "BlockUsersOnBalanceDown": true,
    "ListCompany": [
        300
    ],
    "DataMonthPrice": 0.2,
    "Email": "",
    "FortMonthPrice": 1.0,
    "MaxSmsDayCount": 10,
    "NotFortMonthPrice": 1.0,
    "NotifyDaysCountBeforeBlocking": 7,
    "ShowDebtOnBlockedLogon": true,
    "SmsCost": 0.05,
    "MonthSinglePayment": 0.0,
    "PhoneNumber": "",
    "Result": "Ok"
*/