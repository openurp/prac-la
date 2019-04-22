[#ftl]
{
corporations : [[#list corporations! as corporation]{id : '${corporation.id}', name : '${corporation.name?js_string}'}[#if corporation_has_next],[/#if][/#list]],
pageIndex : ${pageLimit.pageIndex},
pageSize : ${pageLimit.pageSize}
}
