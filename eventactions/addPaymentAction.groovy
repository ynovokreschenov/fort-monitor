def daysUpdate = params.daysUpdate
def fmAccount = cardObject.fmAccount
def blockDate = fmAccount.blockDate

if (params.accept == true){
    if (blockDate != null){
        def payPeriod = fmAccount.periodPay?.code
        def monthDict = ['month':1, 'quarter':3, 'halfyear':6, 'year':12]
        def newBlockDate = api.date.addMonths(blockDate, monthDict[payPeriod])

        def allRentPaid = true
        utils.find('rent$rent', ['removed':false, 'client':fmAccount.fmCompany]).each{
            if (it.paid == false){
                allRentPaid=false
            }
        }
        // ищем незачтенные обещанные платежи, если находим, то вычитаем дни из обещанных платежей
        def wPromisePay = utils.find('wPayment$fmPromisePay', ['fmAccount':fmAccount, 'reckonIn':op.isNull()])
        // считаем количество объектов
        def countObject = countObjectList(fmAccount)
        if (countObject > 0){
        // создаем платеж
            def payment = utils.create('wPayment$fmPayment', [
                'fmAccount':fmAccount,
                'daysUpdate':daysUpdate,
                'fmBalance':daysUpdate*countObject
                ]
            )
        } else {
            utils.throwReadableException("Ошибка получения количества объектов Fort-monitor: ${countObject}", [] as String[], "Ошибка получения количества объектов Fort-monitor: ${countObject}", [] as String[])
        }
        // записываем следующую дату проверки
        if (payment != null){
            utils.edit(fmAccount, ['blockDate':newBlockDate])
            // закрываем обещанные платежи
            if (wPromisePay.size() > 0){
                wPromisePay.each {
                    utils.edit(it, ['reckonIn':payment])
                }
            }
            // разблокируем объекты
            unblockobjects()
        } else {
            utils.throwReadableException("Ошибка создания платежа Fort-monitor", [] as String[], "Ошибка создания платежа Fort-monitor", [] as String[])
        }
    } else {
        utils.throwReadableException("Ошибка: для учетной записи не задана дата блокировки", [] as String[], "Ошибка: для учетной записи не задана дата блокировки", [] as String[])
    }
}


def unblockobjects(){
    // https://glonassagro.com/api/help/index#!/ExternalApiV1/ExternalApiV1_UnblockObjects
    // POST /api/integration/v1/unblockobjects Разблокировка списка объектов
    //def fmAccount = subject.fmAccount
    def fmCompany = fmAccount.fmCompany
    def fmAccountNumber = fmAccount.fmAccountNum
    def objectsIds = utils.find('ou$fmObject', ['removed':false, 'parent':fmCompany]).fmId
    def description = "Разблокировка списка объектов ${subject.title}"

    def sid = modules.fortmonitor.connect().result
    def requestResult = modules.fortmonitor.httpRequest(
        method='post',
        path='unblockobjects',
        query=[
            'description':description
            ],
        body=objectsIds,
        headers=[
            'SessionId':sid
            ]
        ).result

    modules.fortmonitor.disconnect()

    utils.event(subject, "Результат синхронизации: ${requestResult}")
}

// возвращает количество объектов компании
def countObjectList(fmAccount){
  def sid = modules.fortmonitor.connect().result
  def companyId = fmAccount.fmId
  def objectListSize = modules.fortmonitor.httpRequest(
    method = 'get',
    path = 'getobjectslist',
    query = ['companyId':companyId],
    body = [:],
    headers=[
      'SessionId':sid
    ]
  ).result.objects.size()
}