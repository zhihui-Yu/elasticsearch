/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.action.admin.indices.upgrade.post;

import org.elasticsearch.Version;
import org.elasticsearch.action.support.broadcast.BroadcastShardResponse;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.index.shard.ShardId;

import java.io.IOException;
import java.text.ParseException;

/**
 *
 */
class ShardUpgradeResponse extends BroadcastShardResponse {

    private org.apache.lucene.util.Version oldestLuceneSegment;

    private Version upgradeVersion;

    private boolean primary;


    ShardUpgradeResponse() {
    }

    ShardUpgradeResponse(ShardId shardId, boolean primary, Version upgradeVersion, org.apache.lucene.util.Version oldestLuceneSegment) {
        super(shardId);
        this.primary = primary;
        this.upgradeVersion = upgradeVersion;
        this.oldestLuceneSegment = oldestLuceneSegment;
    }

    public org.apache.lucene.util.Version oldestLuceneSegment() {
        return this.oldestLuceneSegment;
    }

    public Version upgradeVersion() {
        return this.upgradeVersion;
    }

    public boolean primary() {
        return primary;
    }


    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        primary = in.readBoolean();
        upgradeVersion = Version.readVersion(in);
        try {
            oldestLuceneSegment = org.apache.lucene.util.Version.parse(in.readString());
        } catch (ParseException ex) {
            throw new IOException("failed to parse lucene version [" + oldestLuceneSegment + "]", ex);
        }

    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeBoolean(primary);
        Version.writeVersion(upgradeVersion, out);
        out.writeString(oldestLuceneSegment.toString());
    }

}