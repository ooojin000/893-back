package com.samyookgoo.palgoosam.bid.service.response;

import com.samyookgoo.palgoosam.common.lock.LockInfo;

public record LockReleaseEvent(LockInfo lockInfo) {
}