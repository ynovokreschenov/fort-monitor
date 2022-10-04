// https://glonassagro.com/api/help/index#!/ExternalApiV1/ExternalApiV1_BlockObjects
// POST /api/integration/v1/blockobjects Блокировка списка объектов

def fmAccount = subject//.fmAccount
def fmCompany = fmAccount.fmCompany
def fmAccountNumber = fmAccount.fmAccountNum
def objectsIds = utils.find('ou$fmObject', ['removed':false, 'parent':fmCompany]).fmId
def description = "Блокировка списка объектов ${subject.title}"

def sid = modules.fortmonitor.connect().result
def requestResult = modules.fortmonitor.httpRequest(
    method='post',
    path='blockobjects',
    query=[
        'description':description
        ],
    body=objectsIds,
    headers=[
        'SessionId':sid
        ]
    ).result

modules.fortmonitor.disconnect()

utils.event(subject, "Результат синхронизации: ${requestResult}");