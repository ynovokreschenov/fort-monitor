def PARAMS_FOR_UPDATE_ON_FORMS = []
if (form == null){
    return PARAMS_FOR_UPDATE_ON_FORMS
}

def fmAccount = cardObject.fmAccount
def blockDate = fmAccount.blockDate
def payPeriod = fmAccount.periodPay?.code
def monthDict = ['month':1, 'quarter':3, 'halfyear':6, 'year':12]
def newBlockDate = api.date.addMonths(blockDate, monthDict[payPeriod])
  
def today = new Date()
def daysUpdate = newBlockDate - today

def allRentPaid = true
utils.find('rent$rent', ['removed':false, 'client':fmAccount.company]).each{
    if (it.paid == false){
        allRentPaid = false
    }
}
// ищем незачтенные обещанные платежи, если находим, то вычитаем дни из обещанных платежей
def wPromisePay = utils.find('wPayment$fmPromisePay', ['fmAccount':fmAccount, 'reckonIn':op.isNull()])
if (wPromisePay.size() > 0){
    wPromisePay.each {
        daysUpdate-=it.daysUpdate
    }
}
return daysUpdate