// https://glonassagro.com/api/help/index#!/ExternalApiV1/ExternalApiV1_UnblockObjects
// POST /api/integration/v1/unblockobjects Разблокировка списка объектов

def fmAccount = subject//.fmAccount
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

utils.event(subject, "Результат синхронизации: ${requestResult}");